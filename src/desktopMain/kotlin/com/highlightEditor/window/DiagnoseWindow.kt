package com.highlightEditor.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.highlightEditor.editor.diagnostics.DiagnosticPopup
import com.highlightEditor.editor.docTree.DocumentType
import kotlinx.coroutines.launch

@Composable
fun DiagnoseWindow(state: EditorWindowState) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.editorState.textState.text.text) {
        println("CALL DIAGNOSE")
        state.diagnoseText()
        println("END DIAGNOSE")
    }

    if (state.diagnosticState.diagnosticPopupState.isVisible) {
        println("HERE")
        DiagnosticPopup(
            offsetTop = 180f,//offsetTop,
            editorState = state.editorState,
            diagnosticState = state.diagnosticState,
            handleTextChange = { v, range, textFix ->
                scope.launch { state.setText(v, DocumentType.TEXT, range, textFix) }
            }
        )
    }
}