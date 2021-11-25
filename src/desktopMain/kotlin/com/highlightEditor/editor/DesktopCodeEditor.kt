package com.highlightEditor.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.highlightEditor.fork.text.BasicTextField

@Composable
internal actual fun CodeEditorImpl(
    content: String,
    modifier: Modifier,
    onTextChange: (String) -> Unit
) {
    MaterialTheme() {
        Surface(Modifier.fillMaxSize()) {
            Column(modifier) {
                BasicTextField(
                    modifier = Modifier.fillMaxSize(),
                    value = content,
                    onValueChange = onTextChange
                )
            }
        }
    }
}
