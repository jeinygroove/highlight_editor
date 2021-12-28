package com.highlightEditor

import com.highlightEditor.editor.diagnostics.DiagnosticElement
import com.highlightEditor.editor.diagnostics.TextAnalyzer

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
class AnalyzeResponse(
    val status: String,
    val response: AnalyzeResult
)

@Serializable
class AnalyzeResult(
    val result: String,
    val errors: List<AnalyzeError>
)

@Serializable
class AnalyzeError(
    val id: String,
    val offset: Int,
    val length: Int,
    val bad: String,
    val better: List<String>,
    val type: String
)

class SampleTextAnalyzer(private val client: HttpClient): TextAnalyzer {
    override suspend fun analyze(text: String): List<DiagnosticElement> {
        // TODO Don't forget to replace "KEY" with the real one
        val response: AnalyzeResponse = client.post("https://api.textgears.com/grammar?key=KEY&language=en-GB") {
            contentType(ContentType.Application.Json)
            body = buildJsonObject {
                put("text", text)
            }
        }

        return response.response.errors.map { err ->
            DiagnosticElement(
                offset = err.offset,
                length = err.length,
                message = err.better.getOrNull(0) ?: "Nothing to suggest"
            )
        }
    }
}