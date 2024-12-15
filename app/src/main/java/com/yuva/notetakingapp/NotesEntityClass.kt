package com.yuva.notetakingapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Note (
    val title: String = "",
    val description: String = "",
    val timeStamp:Long= System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)

@Entity(foreignKeys = [ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"], onDelete = ForeignKey.CASCADE)]  )
data class ToDoItem(
    var noteId: Int = 0,
    val toDoItem: String = "",
    val isChecked: Boolean = false,
    @PrimaryKey
    val id: Int ,
    val isNewNote: Boolean = true
)
