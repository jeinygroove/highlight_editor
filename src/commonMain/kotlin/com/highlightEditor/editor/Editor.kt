package com.highlightEditor.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CodeEditor(
    editorState: EditorState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTextChange: (String) -> Unit = {}
) = CodeEditorImpl(editorState, modifier, enabled, onTextChange)

internal expect fun CodeEditorImpl(
    editorState: EditorState,
    modifier: Modifier,
    enabled: Boolean,
    onTextChange: (String) -> Unit
)
