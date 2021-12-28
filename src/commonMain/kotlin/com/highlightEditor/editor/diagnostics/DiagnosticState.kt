package com.highlightEditor.editor.diagnostics

import androidx.compose.runtime.mutableStateListOf
import com.highlightEditor.editor.text.TextState

/**
 * Responsible for text diagnostics, keeping and updating its results.
 */
class DiagnosticState(
    private val textState: TextState
) {
    val diagnostics = mutableStateListOf<DiagnosticElement>()

    fun updateList(list: List<DiagnosticElement>) {
        diagnostics.clear()
        diagnostics.addAll(list)
    }
}