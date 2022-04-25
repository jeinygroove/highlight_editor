package com.highlightEditor.editor.diagnostics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.highlightEditor.editor.EditorState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
actual fun DiagnosticPopup(
    offsetTop: Float,
    editorState: EditorState,
    diagnosticState: DiagnosticState,
    handleTextChange: (TextFieldValue, IntRange, String) -> Unit
) {
    val diagnosticElement = diagnosticState.diagnosticPopupState.diagnosticElement
    if (diagnosticElement != null) {
        println(diagnosticState.diagnosticPopupState.placement)
        Popup(
            offset = diagnosticState.diagnosticPopupState.placement.let{ it.copy(y = it.y + 120 + 60 /*offsetTop.toInt()*/ - editorState.scrollState.value, x = it.x + 60) },
            onDismissRequest = diagnosticState.diagnosticPopupState::hide
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth().padding(5.dp),
                shape = RoundedCornerShape(10),
                elevation = 2.dp,
                border = BorderStroke(Dp.Hairline, Color.Gray)
            ) {
                Column(
                    Modifier.padding(5.dp).width(IntrinsicSize.Max)
                ) {
                    if (diagnosticElement.message.isNotBlank()) {
                        Text(text = diagnosticElement.message)
                        Divider(modifier = Modifier.padding(vertical = 3.dp), color = Color.Gray, thickness = 1.dp)
                    }
                    diagnosticElement.suggestions.map {
                        val active = remember { mutableStateOf(false) }
                        Text(
                            text = it,
                            fontSize = 16.sp,
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
                                    println("click!")
                                    val range = IntRange(diagnosticElement.offset, diagnosticElement.offset + diagnosticElement.length - 1)
                                    val v = editorState.textState.text.text.replaceRange(
                                        range,
                                        it
                                    )
                                    println(range)
                                    println(v)
                                    handleTextChange(editorState.textState.text.copy(text = v, selection = TextRange(range.first + it.length)), range, it)
                                }),
                            color = Color(6, 69, 173),
                            style = TextStyle(textDecoration = if (active.value) TextDecoration.Underline else TextDecoration.None)
                        )
                    }
                }
            }
        }
    }
}