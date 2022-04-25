package com.highlightEditor.window

import EditorApplicationState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.window.*
import com.highlightEditor.editor.EditorState
import com.highlightEditor.editor.diagnostics.DiagnosticState
import com.highlightEditor.editor.docTree.DocumentType
import com.highlightEditor.util.AlertDialogResult
import com.highlightEditor.util.Settings
import com.highlightEditor.util.rememberAppResources
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.nio.file.Path

class EditorWindowState(
    private val application: EditorApplicationState,
    val scope: CoroutineScope,
    val diagnosticScope: CoroutineScope,
    path: Path?,
    private val exit: (EditorWindowState) -> Unit
) {
    val settings: Settings get() = application.settings

    val window = WindowState()

    var path by mutableStateOf(path)
        private set

    var isChanged by mutableStateOf(false)
        private set

    var diagnosticInProcess by mutableStateOf(false)
        private set

    var autocompletionInProcess by mutableStateOf(false)
        private set

    val openDialog = DialogState<Path?>()
    val saveDialog = DialogState<Path?>()
    val exitDialog = DialogState<AlertDialogResult>()

    private var _notifications = Channel<EditorWindowNotification>(0)
    val notifications: Flow<EditorWindowNotification> get() = _notifications.receiveAsFlow()

    private val _editorState by mutableStateOf(EditorState(TextFieldValue(""), scope))
    private val _diagnosticState by mutableStateOf(DiagnosticState(_editorState.textState, diagnosticScope))

    val editorState: EditorState
        get() = _editorState
    val diagnosticState: DiagnosticState
        get() = _diagnosticState

    fun setText(value: TextFieldValue, type: DocumentType): IntRange? {
        check(isInit)
        isChanged = true
        return _editorState.textState.updateText(value, type)
    }

    fun setText(value: TextFieldValue, type: DocumentType, changeRange: IntRange, textFix: String): IntRange? {
        check(isInit)
        isChanged = true
        return _editorState.textState.updateText(value, type, changeRange, textFix)
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun autocompleteText() {
        val text = _editorState.textState.text.text
        val prefix = text.split(' ').last()

        val matched = _diagnosticState.updateAutocomplete(text)
        println("Matched ${matched}")
        if (matched) return
        GlobalScope.launch {
            if (!autocompletionInProcess) {
                //autocompletionInProcess = true
                val autocomplete = application.analyzer.autocomplete(text, prefix).getOrNull(0)

                if (autocomplete != null && _editorState.textState.text.text == text) {
                    _diagnosticState.setAutocomplete(autocomplete, text)
                }
                autocompletionInProcess = false
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun diagnoseText() {
        GlobalScope.launch {
            if (!diagnosticInProcess) {
                diagnosticInProcess = true
                val text = _editorState.textState.text.text
                println(application.analyzer.analyze(text))
                var diagnostic = application.analyzer.analyze(text)

                // TODO split by sentences
                println(diagnostic)
                println("HERE")
                println(text)
                println(_editorState.textState.text.text)
                diagnostic = diagnostic.filter { elem ->
                    elem.offset + elem.length < _editorState.textState.text.text.length && _editorState.textState.text.text.subSequence(elem.offset, elem.offset + elem.length) == text.subSequence(elem.offset, elem.offset + elem.length)
                }
                diagnosticState.updateList(diagnostic)
                // TODO do smth so it won't affect typing
                diagnosticInProcess = false
            }
        }
    }


    private suspend fun _setText(value: TextFieldValue) {
        val changeRange = _editorState.textState.updateText(value)
        val diagnostic = application.analyzer.analyze(value.text)
        diagnosticState.updateList(diagnostic)
    }

    var isInit by mutableStateOf(false)
        private set

    fun toggleFullscreen() {
        window.placement = if (window.placement == WindowPlacement.Fullscreen) {
            WindowPlacement.Floating
        } else {
            WindowPlacement.Fullscreen
        }
    }

    suspend fun run() {
        if (path != null) {
            open(path!!)
        } else {
            initNew()
        }
    }

    private suspend fun open(path: Path) {
        isInit = false
        isChanged = false
        this.path = path
        try {
            _setText(TextFieldValue(path.readTextAsync()))
            isInit = true
        } catch (e: Exception) {
            e.printStackTrace()
            _editorState.textState.updateText(TextFieldValue("Cannot read $path"))
            _diagnosticState.updateList(listOf())
        }
    }

    private fun initNew() {
        _editorState.textState.updateText(TextFieldValue(""))//AnnotatedString("I is an apple", listOf(AnnotatedString.Range(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red), 0, 8)))))
        _diagnosticState.updateList(listOf())
        isInit = true
        isChanged = false
    }

    fun newWindow(scope: CoroutineScope, diagnosticScope: CoroutineScope) {
        application.newWindow(scope, diagnosticScope)
    }

    suspend fun open() {
        if (askToSave()) {
            val path = openDialog.awaitResult()
            if (path != null) {
                open(path)
            }
        }
    }

    suspend fun save(): Boolean {
        check(isInit)
        if (path == null) {
            val path = saveDialog.awaitResult()
            if (path != null) {
                save(path)
                return true
            }
        } else {
            save(path!!)
            return true
        }
        return false
    }

    private var saveJob: Job? = null

    private suspend fun save(path: Path) {
        isChanged = false
        this.path = path

        saveJob?.cancel()
        saveJob = path.launchSaving(editorState.textState.text.text)

        try {
            saveJob?.join()
            _notifications.trySend(EditorWindowNotification.SaveSuccess(path))
        } catch (e: Exception) {
            isChanged = true
            e.printStackTrace()
            _notifications.trySend(EditorWindowNotification.SaveError(path))
        }
    }

    suspend fun exit(): Boolean {
        return if (askToSave()) {
            exit(this)
            true
        } else {
            false
        }
    }

    private suspend fun askToSave(): Boolean {
        if (isChanged) {
            when (exitDialog.awaitResult()) {
                AlertDialogResult.Yes -> {
                    if (save()) {
                        return true
                    }
                }
                AlertDialogResult.No -> {
                    return true
                }
                AlertDialogResult.Cancel -> return false
            }
        } else {
            return true
        }

        return false
    }

    fun sendNotification(notification: Notification) {
        application.sendNotification(notification)
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun Path.launchSaving(text: String) = GlobalScope.launch {
    writeTextAsync(text)
}

private suspend fun Path.writeTextAsync(text: String) = withContext(Dispatchers.IO) {
    toFile().writeText(text)
}

private suspend fun Path.readTextAsync() = withContext(Dispatchers.IO) {
    toFile().readText()
}

sealed class EditorWindowNotification {
    class SaveSuccess(val path: Path) : EditorWindowNotification()
    class SaveError(val path: Path) : EditorWindowNotification()
}

class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    val isAwaiting get() = onResult != null

    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}