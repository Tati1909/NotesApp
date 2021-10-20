package com.example.notes.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "title")
    var title: String? = "Text",
    @ColumnInfo(name = "description")
    var description: String? = "Description",
    @ColumnInfo(name = "creation")
    var creationDate: Long = 0
)

// отформатируем дату создания заметки в строку формата валюты с пом.  функции расширения getFormattedPrice()
fun NoteEntity.getFormattedData(): Date =
    Calendar.getInstance().time