package com.highlightEditor.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.highlightEditor.editor.diagnostics.DiagnosticElement
import com.highlightEditor.editor.diagnostics.DiagnosticPopupState
import com.highlightEditor.editor.diagnostics.DiagnosticState
import com.highlightEditor.editor.draw.BackgroundDrawer
import com.highlightEditor.editor.text.OffsetState
import com.highlightEditor.editor.text.TextState
import kotlinx.coroutines.CoroutineScope

class EditorState(
    private val text: String,
    scope: CoroutineScope
) {
    val textState = TextState(text)
    val backgroundDrawer = BackgroundDrawer(Color.Yellow, null, 0)
    val diagnosticState = DiagnosticState(textState)
    val cursorPosition = OffsetState()
    val diagnosticPopupState = DiagnosticPopupState(scope)
    val scrollState = ScrollState(0)

    private val cursorPositionAdjustedWithScroll by derivedStateOf {
        cursorPosition.value.copy(y = cursorPosition.value.y + scrollState.value)
    }

    fun updateDiagnostic(diagnostic: List<DiagnosticElement>) {
        diagnosticState.updateList(diagnostic)
    }

    fun onCursorMove(offset: IntOffset) {
        cursorPosition.set(offset)
        diagnosticPopupState.hide()
        val caretOffset = textState.getOffsetForPosition(cursorPositionAdjustedWithScroll)
        val diagnosticHovered: DiagnosticElement? = diagnosticState.findHoveredElement(caretOffset)

        if (diagnosticHovered != null) {
            diagnosticPopupState.setMessage(diagnosticHovered.message)
            diagnosticPopupState.setSuggestions(diagnosticHovered.suggestions)
            val startRangeOffset: Offset = textState.textLayoutResult?.getCursorRect(diagnosticHovered.offset)?.bottomLeft ?: Offset.Zero
            diagnosticPopupState.placement = IntOffset(startRangeOffset.x.toInt(), startRangeOffset.y.toInt())
            diagnosticPopupState.isVisible = true
        } else {
            diagnosticPopupState.setSuggestions(listOf())
            diagnosticPopupState.hide()
        }
    }
}