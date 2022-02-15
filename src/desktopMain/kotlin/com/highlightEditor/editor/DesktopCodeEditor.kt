package com.highlightEditor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.highlightEditor.editor.docTree.DocumentType
import com.highlightEditor.fork.text.KeyboardOptions
import kotlinx.coroutines.launch

@Composable
internal actual fun CodeEditorImpl(
    editorState: EditorState,
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
                        modifier = Modifier.requiredWidth(100.dp).requiredHeight(50.dp),
                        value =
                        when (documentElementType.value) {
                            DocumentType.HEADER_1 -> "H1"
                            DocumentType.HEADER_2 -> "H2"
                            DocumentType.TEXT -> "text"
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
                        modifier = Modifier.widthIn(min = 100.dp),
                        expanded = showPopup.value,
                        onDismissRequest = {
                            showPopup.value = false
                        }
                    ) {
                            Text(
                                "H1",
                                modifier = Modifier.clickable {
                                    documentElementType.value = DocumentType.HEADER_1
                                    showPopup.value = false
                                }.fillMaxWidth().padding(2.dp))
                            Text(
                                "H2",
                                modifier = Modifier.clickable {
                                    documentElementType.value = DocumentType.HEADER_2
                                    showPopup.value = false
                                }.fillMaxWidth().padding(2.dp))
                            Text(
                                "text",
                                modifier = Modifier.clickable {
                                    documentElementType.value = DocumentType.TEXT
                                    showPopup.value = false
                                }.fillMaxWidth().padding(2.dp))
                    }
                }
            }) {
            Column(modifier) {
                BasicTextField(
                    modifier = Modifier.fillMaxSize().drawBehind {
                        editorState.diagnosticState.diagnostics.map { el ->
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
                    onValueChange = { v -> onTextChange(v, documentElementType.value) },
                    onTextLayout = { it ->
                        editorState.textState.textLayoutResult = it
                    },
                    onScroll = {
                        scope.launch {
                            editorState.scrollState.scrollTo(it.toInt())
                        }
                    },
                    textStyle = TextStyle(fontSize = 28.sp),
                    enabled = enabled
                )
            }
        }
    }
}
