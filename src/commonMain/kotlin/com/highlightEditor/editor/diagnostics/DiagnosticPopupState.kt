package com.highlightEditor.editor.diagnostics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
class DiagnosticPopupState(
    private val scope: CoroutineScope
) {
    var isVisible by mutableStateOf(false)
    var placement by mutableStateOf(IntOffset.Zero)
    private val delay = 500L
    private var job: Job? = null
    var message = ""
        private set
    var suggestions = listOf<String>()
        private set

    fun setMessage(message: String) {
        job?.cancel()
        isVisible = false
        job = scope.launch {
            delay(delay)
            isVisible = true
        }
        this.message = message
    }

    fun setSuggestions(suggestions: List<String>) {
        if (this.suggestions != suggestions) {
            if (suggestions.isNotEmpty()) {
                job?.cancel()
                isVisible = false
                job = scope.launch {
                    delay(delay)
                    isVisible = true
                }
            } else {
                hide()
            }
            this.suggestions = suggestions
        }
    }

    fun hide() {
        job?.cancel()
        isVisible = false
    }
}
