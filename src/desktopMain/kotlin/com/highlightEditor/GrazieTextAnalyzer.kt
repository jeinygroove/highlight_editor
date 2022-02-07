package com.highlightEditor

import ai.grazie.client.common.GrazieOkHTTPClient
import ai.grazie.gec.cloud.CloudGECAggregatedCorrection
import ai.grazie.nlp.langs.Language
import com.highlightEditor.editor.diagnostics.DiagnosticElement
import com.highlightEditor.editor.diagnostics.TextAnalyzer
import com.intellij.grazie.client.common.GrazieHTTPClient

class GrazieTextAnalyzer(): TextAnalyzer {
    override suspend fun analyze(text: String): List<DiagnosticElement> {
        val correction = CloudGECAggregatedCorrection(
            language = Language.ENGLISH,
            remote = CloudGECAggregatedCorrection.Remote(
                server = "https://all.agg.gec.stgn.grazie.aws.intellij.net/",
                token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJHcmF6aWUgQXV0aGVudGljYXRpb24iLCJhY2Nlc3MiOiJBTEwiLCJzdGFnZSI6IjIwMjEtMjAyMiIsInRva2VuX2lkIjoyODk5ODU0NTc3NDEwMTcwMzcsInNlcnZpY2UiOiJJbnRlcm5zaGlwIiwiZXhwIjoxNjU3ODg4ODg0fQ.lsf12p1vXyEVcWde_zvT3NKrQl9NHJZ-su0fhsjWZjbteJOfs3xHZ0RbFLRyCMTgmMRTN27TDU5Sr6kgt0USFsRmVwXrUAAwD1QqSEYvMoTEQ05PUnfNZ1X_AjqWh2CleuHBJEq-jeEF5V-E2CcEb_9CrcFhIHa-AxO4rrL2XOdwXISLBea-dYp6yoht8u7RFvX9sD-eUTf9EO8Wo20yAQm1IGG0sz0o2EZK-MpzTrygPCmAuivSEd2X8OxTTrFmmliYRSdaS0IBiO5Gs6QovJPia4b1d8C_Faru5wSQTPkCdsvjpPQlD7kwqtD7HH0F4aSnzTLhZR-GO-aRBmaMQQ",
                type = GrazieHTTPClient.WithV3Auth.RequestType.Service
            ),
            client = GrazieOkHTTPClient
        )
        val res = correction.correct(listOf(text)).getOrNull(0) ?: return listOf()
        val diagnosticElements = res.corrections.toList().map { it ->
            DiagnosticElement(
                offset = it.errorRange.start,
                length = it.errorRange.length,
                message = it.message,
                suggestions = it.replacements.toList()
            )
        }
        return diagnosticElements
    }
}