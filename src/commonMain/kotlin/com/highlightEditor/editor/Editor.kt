package com.highlightEditor.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CodeEditor(
    editorState: EditorState,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {}
) = CodeEditorImpl(editorState, modifier, onTextChange)

internal expect fun CodeEditorImpl(
    editorState: EditorState,
    modifier: Modifier,
    onTextChange: (String) -> Unit
)
