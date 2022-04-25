package com.highlightEditor.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.highlightEditor.editor.diagnostics.DiagnosticState
import com.highlightEditor.editor.docTree.DocumentType

@Composable
fun CodeEditor(
    editorState: EditorState,
    diagnosticState: DiagnosticState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTextChange: (TextFieldValue, DocumentType) -> Unit = {_, _  -> }
) = CodeEditorImpl(editorState, diagnosticState, modifier, enabled, onTextChange)

internal expect fun CodeEditorImpl(
    editorState: EditorState,
    diagnosticState: DiagnosticState,
    modifier: Modifier,
    enabled: Boolean,
    onTextChange: (TextFieldValue, DocumentType) -> Unit
)
