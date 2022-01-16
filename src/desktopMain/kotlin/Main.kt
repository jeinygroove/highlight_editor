import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.*
import com.highlightEditor.util.LocalAppResources
import com.highlightEditor.util.rememberAppResources
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

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
    CompositionLocalProvider(LocalAppResources provides rememberAppResources()) {
        EditorApplication(rememberApplicationState(SampleTextAnalyzer(createClient())))
    }
}
