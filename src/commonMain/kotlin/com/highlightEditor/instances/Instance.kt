package com.highlightEditor.instances

enum class InstanceType {
    TEXT,
    PARAGRAPH,
    SENTENCE,
    WORD
}

abstract class Instance<T>(
    val type: InstanceType,
    val rangeStart: Int,
    val rangeEnd: Int,
    val instances: List<T>
)