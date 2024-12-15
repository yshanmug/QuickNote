package com.yuva.notetakingapp.repository
import androidx.lifecycle.LiveData
import com.yuva.notetakingapp.Note
import com.yuva.notetakingapp.NoteWithToDoItems
import com.yuva.notetakingapp.ToDoItem
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    val notesWithToDoItems: LiveData<List<NoteWithToDoItems>>
    val allNotes: Flow<List<Note>>
    suspend fun insert(note: Note) : Long
    suspend fun insertToDoItem(toDoItem: ToDoItem)
    suspend fun update(note: Note)
    suspend fun deleteToDoItem(id: Int)
    suspend fun delete(noteId: Int)
}