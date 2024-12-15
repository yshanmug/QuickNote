package com.yuva.notetakingapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
//import com.yuva.notetakingapp.AllNotes
import com.yuva.notetakingapp.Note
import com.yuva.notetakingapp.NoteTakingDao
import com.yuva.notetakingapp.NoteWithToDoItems
import com.yuva.notetakingapp.ToDoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(private val noteDao: NoteTakingDao
): NoteRepository {

    override val notesWithToDoItems: LiveData<List<NoteWithToDoItems>> = noteDao.getAllNotesWithToDoItems().asLiveData()
    override val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    override suspend fun insert(note: Note): Long {
        return noteDao.insertNote(note)
    }

     override suspend fun insertToDoItem(toDoItem: ToDoItem) {
        noteDao.insertToDoItem(toDoItem)
    }

    override suspend fun update(note: Note)
    {
       return noteDao.updateNote(note)
    }

    override suspend fun delete(noteId: Int) {
        noteDao.deleteNote(noteId)
        noteDao.deleteToDoItem(noteId)
    }

    override suspend fun deleteToDoItem(id: Int){
        noteDao.deleteCurrentToDoItem(id)
    }


}