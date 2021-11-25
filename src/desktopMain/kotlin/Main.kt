import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.highlightEditor.editor.CodeEditor

fun main() = application {
    val text = remember { mutableStateOf(content) }

    Window(
        title = "Code Editor",
        onCloseRequest = {
            exitApplication()
        }
    ) {
        CodeEditor(
            content = text.value,
            modifier = Modifier.fillMaxSize(),
            onTextChange = { v -> text.value = v }
        )
    }
}
