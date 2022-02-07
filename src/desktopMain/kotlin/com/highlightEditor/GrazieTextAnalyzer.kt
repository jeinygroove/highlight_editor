package com.highlightEditor

import com.highlightEditor.editor.diagnostics.DiagnosticElement
import com.highlightEditor.editor.diagnostics.TextAnalyzer

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
//import ai.grazie.gec.cloud.CloudGECAggregatedCorrection
//import ai.grazie.client.common.GrazieOkHTTPClient

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

class GrazieTextAnalyzer(): TextAnalyzer {
    override suspend fun analyze(text: String): List<DiagnosticElement> {
        return listOf()//CloudGECAggregatedCorrection()
    }
}