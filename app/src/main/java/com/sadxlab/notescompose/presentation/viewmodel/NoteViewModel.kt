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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val useCases: NoteUseCases
) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _editingNote = MutableStateFlow<Note?>(null)
    val editingNote: StateFlow<Note?> = _editingNote.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadNotes() {
        viewModelScope.launch {
            useCases.getAllNotes().collect {
                _notes.value = it
                _isLoading.value=false
            }
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


    fun loadNoteById(id: Int) {
        viewModelScope.launch {
            _editingNote.value = useCases.getNoteById(id)
        }
    }
}