package com.yuva.notetakingapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.JdkConstants.CursorType



@Dao
interface NoteTakingDao {
    @Insert
    suspend fun insertNote(note: Note)  : (Long)

    @Upsert
    suspend fun insertToDoItem(toDoItem: ToDoItem)

    @Query("delete from Note where id = :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("delete from ToDoItem where noteId = :noteId")
    suspend fun deleteToDoItem(noteId: Int)

    @Query("delete from ToDoItem where id = :id")
    suspend fun deleteCurrentToDoItem(id: Int)

    @Query("select * from Note order by id desc")
    fun getAllNotes(): Flow<List<Note>>

    @Update
    suspend fun updateNote(note: Note)

    @Transaction
    @Query("SELECT * FROM Note")
     fun getAllNotesWithToDoItems(): Flow<List<NoteWithToDoItems>>

}

