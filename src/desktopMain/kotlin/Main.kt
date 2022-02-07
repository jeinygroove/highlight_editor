import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.*
import com.highlightEditor.GrazieTextAnalyzer
import com.highlightEditor.util.LocalAppResources
import com.highlightEditor.util.rememberAppResources

/*
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
 */

fun main() = application {
    CompositionLocalProvider(LocalAppResources provides rememberAppResources()) {
        EditorApplication(rememberApplicationState(GrazieTextAnalyzer())) //SampleTextAnalyzer(createClient())))
    }
}
