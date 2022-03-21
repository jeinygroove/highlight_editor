package com.highlightEditor.editor.docTree

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class DocumentModel {
    var elements: MutableList<DocumentElement> = mutableListOf()

    fun addElement(newElement: DocumentElement) {
        val invIndex = elements.binarySearch { it ->
            if (it.range.first <= newElement.range.first && it.range.last >= newElement.range.first) 0
            else if (newElement.range.first > it.range.first) -1
            else 1
        }
        val index = if (invIndex < 0) -(invIndex + 1) else invIndex
        var insertIndex = index
        if (invIndex < 0) {
            elements.add(-(invIndex + 1), newElement)
            concatElements()
            return
        } else {
            val oldElement = elements[index]
            val insertPos = newElement.range.first - oldElement.range.first
            if (elements[index].type == newElement.type) {
                elements[index] = oldElement.copy(
                    range = IntRange(
                        oldElement.range.first,
                        oldElement.range.last + newElement.value.length
                    ), value = oldElement.value.replaceRange(insertPos, insertPos, newElement.value)
                )
            } else {
                if (insertPos == 0) {
                    elements.add(index, newElement)
                } else if (insertPos == oldElement.value.length) {
                    elements.add(index + 1, newElement)
                    insertIndex += 1
                } else {
                    elements[index] = oldElement.copy(
                        range = IntRange(oldElement.range.first, oldElement.range.first + insertPos - 1),
                        value = oldElement.value.substring(
                            IntRange(
                                0,
                                insertPos - 1
                            )
                        )
                    )
                    elements.add(index + 1, newElement)
                    elements.add(
                        index + 2, oldElement.copy(
                            range = IntRange(
                                newElement.range.last + 1,
                                oldElement.range.last + newElement.value.length
                            ),
                            value = oldElement.value.substring(insertPos)
                        )
                    )
                    insertIndex += 2
                }
            }
        }
        elements = elements.mapIndexed { i: Int, documentElement: DocumentElement ->
            if (i > insertIndex)
                documentElement.copy(
                    range = IntRange(
                        documentElement.range.first + newElement.value.length,
                        documentElement.range.last + newElement.value.length
                    )
                )
            else
                documentElement
        }.toMutableList()
        concatElements()
    }

    fun removeRange(range: IntRange) {
        val elementStart = elements.binarySearch { it ->
            if (it.range.first <= range.first && it.range.last >= range.first) 0
            else if (range.first > it.range.first) -1
            else 1
        }
        val elementEnd = elements.binarySearch { it ->
            if (it.range.first <= range.last && it.range.last >= range.last) 0
            else if (range.last < it.range.first) 1
            else -1
        }
        val newElements = elements.subList(0, elementStart).toMutableList()
        val startElement = elements[elementStart]
        val endElement = elements[elementEnd]
        val rangeLength = range.last - range.first + 1
        if (elementStart == elementEnd && startElement.range != range) {
            newElements.add(
                startElement.copy(
                    value = startElement.value.substring(0, range.first - startElement.range.first)
                        .plus(startElement.value.substring(range.last - startElement.range.first + 1)),
                    range = IntRange(startElement.range.first, startElement.range.last - rangeLength)
                )
            )
            newElements.addAll(elements.subList(elementEnd + 1, elements.size).map { elem ->
                elem.copy(
                    range = IntRange(elem.range.first - rangeLength, elem.range.last - rangeLength)
                )
            })
            elements = newElements
            return
        }
        if (startElement.range.first < range.first) {
            newElements.add(
                startElement.copy(
                    value = startElement.value.substring(0, range.first - startElement.range.first),
                    range = IntRange(startElement.range.first, startElement.range.last - rangeLength)
                )
            )
        }
        if (endElement.range.last > range.last) {
            newElements.add(
                endElement.copy(
                    value = endElement.value.substring(range.last - endElement.range.first),
                    range = IntRange(endElement.range.first - rangeLength, endElement.range.last - rangeLength)
                )
            )
        }
        newElements.addAll(elements.subList(elementEnd + 1, elements.size).map { elem ->
            elem.copy(
                range = IntRange(elem.range.first - rangeLength, elem.range.last - rangeLength)
            )
        })
        elements = newElements
    }

    fun getContent(): AnnotatedString {
        return AnnotatedString(
            elements.joinToString(separator = "", transform = { it.value }),
            elements.map { AnnotatedString.Range(getSpanStyle(it.type), it.range.first, it.range.last + 1) },
            listOf()
        )
    }

    fun concatElements() {
        if (elements.isEmpty() || elements.size == 1)
            return
        var prevType = elements[0].type
        val newElements = mutableListOf<DocumentElement>()
        var currElement: DocumentElement = elements[0]
        var currValue = elements[0].value
        for (i in 1 until elements.size) {
            if (prevType == elements[i].type) {
                currValue += elements[i].value
            } else {
                if (i != 1)
                    currElement = currElement.copy(
                        value = currValue,
                        range = IntRange(currElement.range.first, elements[i - 1].range.last)
                    )
                newElements.add(currElement)
                currElement = elements[i]
                currValue = elements[i].value
                prevType = elements[i].type
            }
        }
        currElement =
            currElement.copy(value = currValue, range = IntRange(currElement.range.first, elements.last().range.last))
        newElements.add(currElement)
        elements = newElements
    }

    private fun getSpanStyle(type: DocumentType): SpanStyle {
        return when (type) {
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