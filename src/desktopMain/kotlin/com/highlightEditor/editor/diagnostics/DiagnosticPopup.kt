package com.highlightEditor.editor.diagnostics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.highlightEditor.fork.gestures.detectTapGestures
import com.highlightEditor.fork.text.mouseDragGestureDetector
import com.highlightEditor.fork.text.selection.MouseSelectionObserver
import com.highlightEditor.fork.text.selection.mouseSelectionDetector

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
actual fun DiagnosticPopup(
    suggestions: List<String>,
    state: DiagnosticPopupState
) {
    state.setSuggestions(suggestions)

    Popup(
        offset = state.placement,
        onDismissRequest = state::hide
    ) {
        Surface(
            modifier = Modifier.wrapContentWidth(),
            elevation = 2.dp,
            border = BorderStroke(Dp.Hairline, Color.Gray)
        ) {
            Column(
                Modifier.padding(5.dp).width(IntrinsicSize.Max)
            ) {
                if (state.message.isNotBlank()) {
                    Text(text = state.message)
                    Divider(color = Color.Black, thickness = 1.dp)
                }
                state.suggestions.map {
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
                            ),
                        color = if (active.value) Color.Cyan else Color.Black,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
    }
}