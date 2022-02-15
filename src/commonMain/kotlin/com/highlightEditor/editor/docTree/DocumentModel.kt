package com.highlightEditor.editor.docTree

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class DocumentModel {
    val elements: MutableList<DocumentElement> = mutableListOf()

    fun addElement(newElement: DocumentElement) {
        val index = elements.binarySearch { it ->
            if (it.range.first < newElement.range.first && it.range.last >= newElement.range.first) 0
            else if (newElement.range.first <= it.range.first) -1
            else 1
        }
        elements.add(index + 1, newElement)
    }

    fun removeRange(range: IntRange) {
        val elementStart = elements.binarySearch { it ->
            if (it.range.first < range.first && it.range.last >= range.first) 0
            else if (range.first <= it.range.first) -1
            else 1
        }
        val elementEnd = elements.binarySearch { it ->
            if (it.range.first < range.last && it.range.last >= range.last) 0
            else if (range.last <= it.range.first) -1
            else 1
        }
        // elements.removeAll
        // elements.add(index + 1, newElement)
    }

    fun getContent(): AnnotatedString {
        return AnnotatedString(elements.joinToString(transform = { it.value }),
            elements.map { AnnotatedString.Range(getSpanStyle(it.type), it.range.first, it.range.last) },
        listOf())
    }

    private fun getSpanStyle(type: DocumentType): SpanStyle {
        return when(type) {
            DocumentType.HEADER_1 -> SpanStyle(
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            DocumentType.HEADER_2 -> SpanStyle(
                color = Color.DarkGray,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic
            )
            DocumentType.TEXT -> SpanStyle(
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

enum class DocumentType {
    HEADER_1,
    HEADER_2,
    TEXT
}

data class DocumentElement(
    val type: DocumentType,
    val value: String,
    val range: IntRange
)