package com.sadxlab.notescompose.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.usecases.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val useCases: NoteUseCases
) : ViewModel() {
    private val _notes = mutableStateOf<List<Note>>(emptyList())
    val notes: State<List<Note>> = _notes

    var editingNote by mutableStateOf<Note?>(null)
        private set

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = useCases.getAllNotes()
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            useCases.addNote(note)
            loadNotes()
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            useCases.addNote(note)
            loadNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            useCases.deleteNote(note)
            loadNotes()
        }
    }

    fun getNoteById(noteId: Int): Note? {
        return notes.value.find { it.id == noteId }
    }

    fun loadNoteById(id: Int) {
        viewModelScope.launch {
            editingNote = useCases.getNoteById(id)
        }
    }
}