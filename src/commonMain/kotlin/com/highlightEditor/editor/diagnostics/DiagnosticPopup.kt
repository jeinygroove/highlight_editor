package com.highlightEditor.editor.diagnostics

import androidx.compose.runtime.Composable

@Composable
expect fun DiagnosticPopup(
    suggestions: List<String>,
    state: DiagnosticPopupState
)