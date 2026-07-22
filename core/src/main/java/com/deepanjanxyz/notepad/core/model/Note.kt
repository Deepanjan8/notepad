package com.deepanjanxyz.notepad.core.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val categoryId: Long = 1,
    val categoryName: String = "General",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val colorHex: String = "#6200EE"
)
