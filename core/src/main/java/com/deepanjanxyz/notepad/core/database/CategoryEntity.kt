package com.deepanjanxyz.notepad.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deepanjanxyz.notepad.core.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#6200EE",
    val iconName: String = "folder"
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        colorHex = colorHex,
        iconName = iconName
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        colorHex = colorHex,
        iconName = iconName
    )
}
