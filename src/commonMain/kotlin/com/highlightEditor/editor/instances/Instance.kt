package com.highlightEditor.editor.instances

// TODO replace with grazie tokenizer
abstract class TextInstance<T> {
    var range: IntRange
    var content: MutableList<T>

    constructor(range: IntRange = IntRange.EMPTY, content: MutableList<T> = mutableListOf()) {
        this.range = range
        this.content = content
    }

    constructor(str: String, offset: Int) {
        this.range = IntRange.EMPTY
        this.content = mutableListOf()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TextInstance<*>)
            return false
        return range == other.range && content == other.content
    }

    override fun hashCode(): Int {
        var result = 7
        result = 31 * result + range.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }
}

class Word: TextInstance<Char> {
    constructor(range: IntRange = IntRange.EMPTY, content: MutableList<Char> = mutableListOf()): super()

    constructor(str: String, offset: Int) {
        this.range = IntRange(offset, offset + str.length)

        this.content = str.toCharArray().toMutableList()
    }
}
class Sentence: TextInstance<Word> {
    constructor(range: IntRange = IntRange.EMPTY, content: MutableList<Char> = mutableListOf()): super()

    constructor(str: String, offset: Int) : super(str, offset) {
        if (str.matches(Regex("[^.!?]+[.!?]+"))) throw Exception("It's not a sentence")

        this.range = IntRange(offset, offset + str.length)

        var off = offset
        this.content = str.split("(?=\\\\b[ ]+)").mapIndexed { _: Int, s: String ->
            val word = Word(s, off)
            off += s.length
            word
        }.toMutableList()
    }
}

class Paragraph: TextInstance<Sentence> {
    constructor(range: IntRange = IntRange.EMPTY, content: MutableList<Char> = mutableListOf()): super()

    constructor(str: String, offset: Int): super(str, offset) {
        if (str.matches(Regex("[^\n]+[\n]+"))) throw Exception("It's not a paragraph")

        this.range = IntRange(offset, offset + str.length)

        var off = offset
        this.content = str.split(Regex("(?=\\\\b[.!?]+)")).mapIndexed { _: Int, s: String ->
            val sentence = Sentence(s, off)
            off += s.length
            sentence
        }.toMutableList()
    }
}

class Text: TextInstance<Paragraph> {
    constructor(range: IntRange = IntRange.EMPTY, content: MutableList<Char> = mutableListOf()): super()

    constructor(str: String, offset: Int): super(str, offset) {
        this.range = IntRange(offset, offset + str.length)

        var off = offset
        this.content = str.split(Regex("(?=\\\\b[\n]+)")).mapIndexed { _: Int, s: String ->
            val paragraph = Paragraph(s, off)
            off += s.length
            paragraph
        }.toMutableList()
    }
}