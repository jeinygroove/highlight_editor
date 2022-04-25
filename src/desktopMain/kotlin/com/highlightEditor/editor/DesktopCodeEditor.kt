package com.highlightEditor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import com.highlightEditor.fork.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.highlightEditor.editor.diagnostics.AutocompletionTransformation
import com.highlightEditor.editor.diagnostics.DiagnosticState
import com.highlightEditor.editor.docTree.DocumentType
import kotlinx.coroutines.launch

@Composable
internal actual fun CodeEditorImpl(
    editorState: EditorState,
    diagnosticState: DiagnosticState,
    modifier: Modifier,
    enabled: Boolean,
    onTextChange: (TextFieldValue, DocumentType) -> Unit
) {
    val scope = rememberCoroutineScope()

    val documentElementType = remember { mutableStateOf(DocumentType.TEXT) }
    val showPopup = remember { mutableStateOf(false) }
    MaterialTheme {
        Scaffold(Modifier.fillMaxSize(),
            topBar = {
                Column(Modifier.background(Color.LightGray).fillMaxWidth().padding(5.dp)) {
                    TextField(
                        modifier = Modifier.requiredWidth(150.dp).requiredHeight(50.dp),
                        value = when (documentElementType.value) {
                            DocumentType.HEADER -> "header"
                            DocumentType.TEXT -> "text"
                            DocumentType.LINK -> "link"
                            DocumentType.LIST -> "list"
                        },
                        onValueChange = { },
                        trailingIcon = {
                            IconButton(
                                onClick = { showPopup.value = !showPopup.value }
                            ) {
                                Icon(
                                    imageVector = if (showPopup.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    ""
                                )
                            }
                        },
                        readOnly = true,
                        textStyle = TextStyle(fontSize = 16.sp)
                    )
                    DropdownMenu(
                        modifier = Modifier.widthIn(min = 150.dp),
                        expanded = showPopup.value,
                        onDismissRequest = {
                            showPopup.value = false
                        }
                    ) {
                        Text(
                            "header",
                            modifier = Modifier.clickable {
                                editorState.textState.makeType(documentElementType.value, DocumentType.HEADER)
                                documentElementType.value = DocumentType.HEADER
                                showPopup.value = false
                            }.fillMaxWidth().padding(2.dp)
                        )
                        Text(
                            "text",
                            modifier = Modifier.clickable {
                                editorState.textState.makeType(documentElementType.value, DocumentType.TEXT)
                                documentElementType.value = DocumentType.TEXT
                                showPopup.value = false
                            }.fillMaxWidth().padding(2.dp)
                        )
                        Text(
                            "link",
                            modifier = Modifier.clickable {
                                editorState.textState.makeType(documentElementType.value, DocumentType.LINK)
                                documentElementType.value = DocumentType.LINK
                                showPopup.value = false
                            }.fillMaxWidth().padding(2.dp)
                        )
                        Text(
                            "list",
                            modifier = Modifier.clickable {
                                editorState.textState.makeType(documentElementType.value, DocumentType.LIST)
                                documentElementType.value = DocumentType.LIST
                                showPopup.value = false
                            }.fillMaxWidth().padding(2.dp)
                        )
                    }
                }
            }) {
            Column(modifier) {
                BasicTextField(
                    modifier = Modifier.fillMaxSize().drawBehind {
                        diagnosticState.diagnostics.filter { it.length != 0 }.map { el ->
                            editorState.textState.getPositionForTextRange(
                                IntRange(el.offset, el.offset + el.length - 1)
                            )?.let {
                                editorState.backgroundDrawer.draw(
                                    it.map { el -> el.copy(-editorState.scrollState.value.toFloat()) }, this
                                )
                            }
                        }
                    },
                    value = editorState.textState.text,
                    onValueChange = { v ->
                        run {
                            println("UPDATE")
                            if (v.text != editorState.textState.text.text) {
                                editorState.textState.textLayoutResult = null
                                diagnosticState.updateList(listOf())
                            }
                            onTextChange(v, documentElementType.value)
                        }
                    },
                    onTextLayout = { layoutRes ->
                        println(layoutRes.layoutInput.text == editorState.textState.text.annotatedString)
                        editorState.textState.textLayoutResult = layoutRes
                    },
                    onScroll = {
                        scope.launch {
                            editorState.scrollState.scrollTo(it.toInt())
                        }
                    },
                    visualTransformation = AutocompletionTransformation(diagnosticState.autocompletion.autocomplete.value),
                    textStyle = TextStyle(fontSize = 28.sp),
                    enabled = enabled
                )
            }
        }
    }
}
