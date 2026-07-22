package com.deepanjanxyz.notepad.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deepanjanxyz.notepad.core.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "category_id")
    val categoryId: Long = 1,
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#6200EE"
)

fun NoteEntity.toDomain(categoryName: String = "General"): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        categoryId = categoryId,
        categoryName = categoryName,
        isPinned = isPinned,
        isArchived = isArchived,
        colorHex = colorHex
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        categoryId = categoryId,
        isPinned = isPinned,
        isArchived = isArchived,
        colorHex = colorHex
    )
}
