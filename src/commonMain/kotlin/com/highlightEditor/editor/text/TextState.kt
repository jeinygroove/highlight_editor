package com.highlightEditor.editor.text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toOffset
import com.highlightEditor.editor.docTree.DocumentElement
import com.highlightEditor.editor.docTree.DocumentModel
import com.highlightEditor.editor.docTree.DocumentType
import com.highlightEditor.editor.instances.Text
import kotlin.math.abs
import kotlin.math.min

/**
 * Responsible for text content, positions/ranges in text field layout.
 */
class TextState(
    text: TextFieldValue
) : TextRangesHelper {
    var text by mutableStateOf(text)
    var textLayoutResult by mutableStateOf<TextLayoutResult?>(null)
    private var prevSelection: TextRange = text.selection
    var documentModel: DocumentModel = DocumentModel()

    fun updateText(newTextFieldValue: TextFieldValue, type: DocumentType = DocumentType.TEXT) {
        val newSelection = newTextFieldValue.selection
        // means that we typed (or deleted smth)
        if (newTextFieldValue.text != text.text) {
            val diffInLengths = newTextFieldValue.text.length - text.text.length
            if (diffInLengths < 0) {
                val changeRange = IntRange(newSelection.start, newSelection.start + abs(diffInLengths) - 1)
                documentModel.removeRange(changeRange)
            } else {
                val changeRange = IntRange(text.selection.start, text.selection.start + abs(diffInLengths) - 1)
                documentModel.addElement(DocumentElement(type, newTextFieldValue.text.substring(changeRange), changeRange))
            }
        }
        prevSelection = text.selection
        text = newTextFieldValue.copy(annotatedString = documentModel.getContent())
    }

    override fun getPositionForTextRange(range: IntRange): Position? {
        return textLayoutResult?.let { textLayoutResult ->
            val startLine = textLayoutResult.getLineForOffset(range.first)
            val endLine = textLayoutResult.getLineForOffset(range.last)
            val startCharacterTopLeft = textLayoutResult.getBoundingBox(range.first).topLeft
            val endCharacterBottomRight = textLayoutResult.getBoundingBox(range.last).bottomRight

            return if (startLine == endLine) {
                listOf(TextSegment(startLine, Rect(startCharacterTopLeft, endCharacterBottomRight)))
            } else {
                val list = mutableListOf<TextSegment>()
                list.add(
                    TextSegment(
                        startLine,
                        Rect(
                            startCharacterTopLeft,
                            Offset(textLayoutResult.getLineRight(startLine), textLayoutResult.getLineBottom(startLine))
                        )
                    )
                )
                var line = startLine + 1
                while (line < endLine) {
                    val lineBoundingBox = Rect(
                        Offset(
                            textLayoutResult.getLineLeft(line),
                            textLayoutResult.getLineTop(line)
                        ),
                        Offset(
                            textLayoutResult.getLineRight(line),
                            textLayoutResult.getLineBottom(line)
                        )
                    )
                    list.add(
                        TextSegment(
                            line,
                            lineBoundingBox
                        )
                    )
                    line++
                }
                list.add(
                    TextSegment(
                        endLine,
                        Rect(
                            Offset(textLayoutResult.getLineLeft(endLine), textLayoutResult.getLineTop(endLine)),
                            endCharacterBottomRight
                        )
                    )
                )
                list
            }
        }
    }

    override fun getOffsetForPosition(cursorPosition: IntOffset): Int {
        return textLayoutResult?.let { layoutResult ->
            if (cursorPosition.x < 0 || cursorPosition.y < 0 || cursorPosition.y > layoutResult.size.height) {
                return -1
            }
            val offsetForPosition = layoutResult.getOffsetForPosition(cursorPosition.toOffset())
            val lineIndex = layoutResult.getLineForOffset(offsetForPosition)
            if (cursorPosition.x > layoutResult.getLineRight(lineIndex)
                || cursorPosition.y > layoutResult.getLineBottom(lineIndex)
                || cursorPosition.y < layoutResult.getLineTop(lineIndex)
            ) {
                return -1
            }
            return offsetForPosition
        } ?: -1
    }
}