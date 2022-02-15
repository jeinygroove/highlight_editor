package com.highlightEditor.editor.diagnostics

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import com.highlightEditor.editor.EditorState

@Composable
expect fun DiagnosticPopup(
    editorState: EditorState,
    handleTextChange: (TextFieldValue) -> Unit = {}
)