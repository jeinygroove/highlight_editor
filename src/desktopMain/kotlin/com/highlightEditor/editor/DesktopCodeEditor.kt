package com.highlightEditor.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import com.highlightEditor.fork.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
internal actual fun CodeEditorImpl(
    editorState: EditorState,
    modifier: Modifier,
    enabled: Boolean,
    onTextChange: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(modifier) {
                BasicTextField(
                    modifier = Modifier.fillMaxSize().drawBehind {
                        editorState.diagnosticState.diagnostics.map { el ->
                            editorState.textState.getPositionForTextRange(
                                IntRange(el.offset, el.offset + el.length - 1)
                            )?.let {
                                editorState.backgroundDrawer.draw(
                                    it.map { el -> el.copy(-editorState.scrollState.value.toFloat()) }, this
                                )
                            }
                        }
                    },
                    value = editorState.textState.text,
                    onValueChange = onTextChange,
                    onTextLayout = { it ->
                        editorState.textState.textLayoutResult = it
                    },
                    onScroll = {
                        scope.launch {
                            editorState.scrollState.scrollTo(it.toInt())
                        }
                    },
                    textStyle = TextStyle(fontSize = 28.sp),
                    enabled = enabled
                )
            }
        }
    }
}
