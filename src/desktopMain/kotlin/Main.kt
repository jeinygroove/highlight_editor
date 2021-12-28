import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.highlightEditor.SampleTextAnalyzer
import com.highlightEditor.editor.CodeEditor
import com.highlightEditor.editor.EditorState
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.coroutines.launch

fun createClient(): HttpClient {
    return HttpClient(CIO) {
        expectSuccess = false
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                json = kotlinx.serialization.json.Json {
                    useArrayPolymorphism = true
                    isLenient = true
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}

fun main() = application {
    val txt = content1
    val scope = rememberCoroutineScope()
    val editorState = remember { mutableStateOf(EditorState(txt, scope)) }
    val analyzer = SampleTextAnalyzer(createClient())

    scope.launch {
        val diagnostic = analyzer.analyze(editorState.value.textState.text)
        editorState.value.updateDiagnostic(diagnostic)
    }

    Window(
        title = "Editor",
        onCloseRequest = {
            exitApplication()
        }
    ) {
        CodeEditor(
            editorState = editorState.value,
            modifier = Modifier.fillMaxSize(),
            onTextChange = { v ->
                scope.launch {
                    editorState.value.updateDiagnostic(analyzer.analyze(editorState.value.textState.text))
                }
                editorState.value.textState.text = v
            }
        )
    }
}
