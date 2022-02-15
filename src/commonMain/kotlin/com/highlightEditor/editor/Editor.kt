package com.highlightEditor.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.highlightEditor.editor.docTree.DocumentType

@Composable
fun CodeEditor(
    editorState: EditorState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTextChange: (TextFieldValue, DocumentType) -> Unit = {_, _  -> }
) = CodeEditorImpl(editorState, modifier, enabled, onTextChange)

internal expect fun CodeEditorImpl(
    editorState: EditorState,
    modifier: Modifier,
    enabled: Boolean,
    onTextChange: (TextFieldValue, DocumentType) -> Unit
)
