package com.highlightEditor.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.highlightEditor.fork.text.BasicTextField

@Composable
internal actual fun CodeEditorImpl(
    editorState: EditorState,
    modifier: Modifier,
    onTextChange: (String) -> Unit
) {
    val scrollOffsetY = remember { mutableStateOf(0f) }
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(modifier) {
                BasicTextField(
                    modifier = Modifier.fillMaxSize().drawBehind {
                        editorState.diagnosticState.diagnostics.map { el ->
                            editorState.textState.getPositionForTextRange(
                                IntRange(el.offset, el.offset + el.length)
                            )?.let {
                                editorState.backgroundDrawer.draw(
                                    it.map { el -> el.copy(-scrollOffsetY.value) }, this
                                )
                            }
                        }
                    },
                    value = editorState.textState.text,
                    onValueChange = onTextChange,
                    onTextLayout = { it ->
                        editorState.textState.textLayoutResult = it
                    },
                    onScroll = { it ->
                        scrollOffsetY.value = it
                    },
                    textStyle = TextStyle(fontSize = 28.sp)
                )
            }
        }
    }
}
