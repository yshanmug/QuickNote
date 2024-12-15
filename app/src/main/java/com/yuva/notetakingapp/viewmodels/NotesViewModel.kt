package com.yuva.notetakingapp.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuva.notetakingapp.Note
import com.yuva.notetakingapp.NoteWithToDoItems
import com.yuva.notetakingapp.ToDoItem
import com.yuva.notetakingapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Math.random
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class NotesViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {
    private var _note = MutableStateFlow(NoteWithToDoItems(note = Note(), toDoItems = emptyList()))
    val note = _note.asStateFlow()

    private var _toDoListItems = MutableStateFlow<List<ToDoItem>>(emptyList())
    val toDoListItems = _toDoListItems.asStateFlow()

    private var _allNotesList = MutableStateFlow<List<NoteWithToDoItems>>(value = emptyList())
    val allNotesList = _allNotesList.asStateFlow()

    private var _isDataReady = MutableStateFlow(true)
    val isDataReady = _isDataReady.asStateFlow()
//    val notesWithToDoItems: LiveData<List<NoteWithToDoItems>> = noteRepository.notesWithToDoItems


    init {
        getNotes()
    }


    private fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.notesWithToDoItems.asFlow().collect() { noteList ->
                _allNotesList.update { noteList }
                Log.d("NotesPrinted", noteList.toString())
                delay(300)
                _isDataReady.value = false
            }
        }
    }

    fun addToDoItem(noteId: Int, toDoText: String, isChecked: Boolean) {
        val newItem = ToDoItem(
            noteId = noteId,
            toDoItem = toDoText,
            isChecked = isChecked,
            id = System.currentTimeMillis().toInt()
        )
        _toDoListItems.value += newItem
    }

    fun updateToDoItem(toDoItem: ToDoItem) {
        _toDoListItems.value = _toDoListItems.value.map {

            if (it.id == toDoItem.id) {
                toDoItem.copy(isNewNote = false)
                toDoItem
            } else
                it
        }
    }

    fun deletedToDoItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val updatedToDoList = _toDoListItems.value.filter { it.id != id }
            _toDoListItems.value = updatedToDoList
            noteRepository.deleteToDoItem(id)
            val isNoteEmpty =
                note.value.note.title.isEmpty() && updatedToDoList.none { it.toDoItem.isNotEmpty() }
            if (isNoteEmpty) {
                noteRepository.delete(note.value.note.id)
                _note.value = NoteWithToDoItems(note = Note(), toDoItems = emptyList())

            }
        }
    }

    private fun insertToDoItems(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO)
        {
            _toDoListItems.value.forEach {
            }
            val isNewNote = _toDoListItems.value.all {
                it.isNewNote
            }

            _toDoListItems.value.forEach {
                it.noteId = noteId
                Log.d("currentItem2", it.toString())
                if (it.toDoItem.isNotEmpty()) {
                    noteRepository.insertToDoItem(it)
                }
            }
            _toDoListItems.value = (emptyList())
        }
    }

    fun insertNote(isToDoList: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isToDoList) {
                var toDoItems = toDoListItems.value.all { it.toDoItem.isEmpty() }
                if ((note.value.note.title.isNotEmpty() && _toDoListItems.value.any { it.toDoItem.isNotEmpty() })
                    || (note.value.note.title.isNotEmpty() || _toDoListItems.value.any { it.toDoItem.isNotEmpty() })
                ) {
                    val noteId =
                        if (note.value.note.id != 0) {
                            noteRepository.update(note.value.note)
                            _note.value.note.id.toLong()
                        } else {
                            noteRepository.insert(note.value.note)
                        }

                    insertToDoItems(noteId.toInt())

                    _note.value = NoteWithToDoItems(note = Note(), toDoItems = emptyList())
                } else {
                    _toDoListItems.value = emptyList()
                }
            } else {
                if ((note.value.note.title.isNotEmpty() && note.value.note.description.isNotEmpty())
                    || (note.value.note.title.isNotEmpty() || note.value.note.description.isNotEmpty())
                ) {
                    if (note.value.note.id != 0) {
                        noteRepository.update(note.value.note)
                        _note.value.note.id
                    } else {
                        noteRepository.insert(note.value.note)
                    }
                    _note.value = NoteWithToDoItems(note = Note(), toDoItems = emptyList())
                } else {
                    noteRepository.delete(note.value.note.id)
                    _note.value = NoteWithToDoItems(note = Note(), toDoItems = emptyList())


                }

            }
        }
    }


    fun sortNoteByTitle(isAscending: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val notesForSorting = noteRepository.notesWithToDoItems.value
            Log.d("notesForSorting", notesForSorting.toString())
            val allTitleEmpty = notesForSorting?.all { it.note.title.isEmpty() } ?: true
            if (notesForSorting != null) {
                _allNotesList.value =
                    if (allTitleEmpty)
                        if (isAscending)
                            notesForSorting.sortedBy {
                                if (it.note.description.isEmpty())
                                    it.toDoItems.first().toDoItem.lowercase()
                                else
                                    it.note.description.lowercase()
                            }
                        else
                            notesForSorting.sortedByDescending {
                                if (it.note.description.isEmpty())
                                    it.toDoItems.first().toDoItem.lowercase()
                                else
                                    it.note.description.uppercase()
                            }
                    else
                        if (isAscending)
                            notesForSorting.sortedBy {
                                if (it.note.title.isEmpty())
                                    "~"
                                else
                                    it.note.title.lowercase()
                            }
                        else
                            notesForSorting.sortedByDescending {
                                if (it.note.title.isEmpty())
                                    "~"
                                else
                                    it.note.title.lowercase()
                            }
            }
            Log.d("AfterSorteddd", _allNotesList.value.toString())
        }
    }


    fun sortByTimeCreated(isAscending: Boolean) {
        viewModelScope.launch(Dispatchers.IO)
        {
            val notesListForSortingByTime = noteRepository.allNotes.first()
            Log.d("sortNoteByTime: ", notesListForSortingByTime.toString())
            val notesForSortByTime = noteRepository.notesWithToDoItems.asFlow().firstOrNull()
            Log.d("nForSorting", notesForSortByTime.toString())
            if (notesForSortByTime != null) {
                _allNotesList.value =
                    if (isAscending)
                        notesForSortByTime
                    else
                        notesForSortByTime.reversed()
            }

        }
    }


    fun setNote(noteWithToDoItems: NoteWithToDoItems) {
        _note.value = noteWithToDoItems
        _toDoListItems.value = noteWithToDoItems.toDoItems
        Log.d("SetNote", "${_note.value}")
    }


    fun updateTitle(title: String) {
//        _note.value = note.value.copy(Note(title = title))
        val currentNoteWithToDoItems = _note.value
        _note.value = currentNoteWithToDoItems.copy(
            note = currentNoteWithToDoItems.note.copy(title = title)
        )
    }

    fun updateDescription(description: String) {
        val currentNoteWithToDoItems = _note.value
        _note.value = currentNoteWithToDoItems.copy(
            note = currentNoteWithToDoItems.note.copy(description = description)
        )
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.delete(noteId)
        }
    }


}