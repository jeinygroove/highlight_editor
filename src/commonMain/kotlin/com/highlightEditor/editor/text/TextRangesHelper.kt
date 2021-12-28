package com.highlightEditor.editor.text

/**
 * Contains all useful methods for translating text ranges to layout positions and vice versa
 */
interface TextRangesHelper {
    fun getPositionForTextRange(range: IntRange): Position?
}