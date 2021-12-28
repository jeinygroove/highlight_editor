package com.highlightEditor.editor.text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import com.highlightEditor.editor.instances.Text

/**
 * Responsible for text content, positions/ranges in text field layout.
 */
class TextState(
    text: String
) : TextRangesHelper {
    var text by mutableStateOf(text)
    var textLayoutResult by mutableStateOf<TextLayoutResult?>(null)

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
}