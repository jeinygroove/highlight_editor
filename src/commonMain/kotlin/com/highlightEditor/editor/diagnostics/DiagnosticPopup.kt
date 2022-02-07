package com.highlightEditor.editor.diagnostics

import androidx.compose.runtime.Composable
import com.highlightEditor.editor.EditorState

@Composable
expect fun DiagnosticPopup(
    editorState: EditorState,
    handleTextChange: (String) -> Unit = {}
)