package com.highlightEditor.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CodeEditor(
    content: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {}
) = CodeEditorImpl(content, modifier, onTextChange)

internal expect fun CodeEditorImpl(
    content: String,
    modifier: Modifier,
    onTextChange: (String) -> Unit
)
