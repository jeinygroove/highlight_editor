package com.highlightEditor.editor

import androidx.compose.ui.graphics.Color
import com.highlightEditor.editor.diagnostics.DiagnosticElement
import com.highlightEditor.editor.diagnostics.DiagnosticState
import com.highlightEditor.editor.draw.BackgroundDrawer
import com.highlightEditor.editor.text.TextState

class EditorState(
    private val text: String,
) {
    val textState = TextState(text)
    val backgroundDrawer = BackgroundDrawer(Color.Yellow, null, 0)
    val diagnosticState = DiagnosticState(textState)

    fun updateDiagnostic(diagnostic: List<DiagnosticElement>) {
        diagnosticState.updateList(diagnostic)
    }
}