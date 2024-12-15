package com.yuva.notetakingapp

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithToDoItems(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val toDoItems: List<ToDoItem>
)