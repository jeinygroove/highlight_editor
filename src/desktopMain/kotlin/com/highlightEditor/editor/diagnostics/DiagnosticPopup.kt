package com.highlightEditor.editor.diagnostics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.highlightEditor.editor.EditorState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
actual fun DiagnosticPopup(
    editorState: EditorState,
    handleTextChange: (TextFieldValue) -> Unit
) {
    val diagnosticElement = editorState.diagnosticPopupState.diagnosticElement
    if (diagnosticElement != null) {
        Popup(
            offset = editorState.diagnosticPopupState.placement.copy(y = editorState.diagnosticPopupState.placement.y + 60),
            onDismissRequest = editorState.diagnosticPopupState::hide
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth(),
                elevation = 2.dp,
                border = BorderStroke(Dp.Hairline, Color.Gray)
            ) {
                Column(
                    Modifier.padding(5.dp).width(IntrinsicSize.Max)
                ) {
                    if (diagnosticElement.message.isNotBlank()) {
                        Text(text =diagnosticElement.message)
                        Divider(color = Color.Black, thickness = 1.dp)
                    }
                    diagnosticElement.suggestions.map {
                        val active = remember { mutableStateOf(false) }
                        Text(
                            text = it,
                            modifier = Modifier
                                .pointerMoveFilter(
                                    onEnter = {
                                        active.value = true
                                        false
                                    },
                                    onExit = {
                                        active.value = false
                                        false
                                    }
                                ).clickable(onClick = {
                                    val range = IntRange(diagnosticElement.offset, diagnosticElement.offset + diagnosticElement.length - 1)
                                    val v = editorState.textState.text.text.replaceRange(
                                        range,
                                        it
                                    )
                                    handleTextChange(editorState.textState.text.copy(text = v, selection = TextRange(range.first + it.length)))
                                }),
                            color = if (active.value) Color.Cyan else Color.Black,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
            }
        }
    }
}