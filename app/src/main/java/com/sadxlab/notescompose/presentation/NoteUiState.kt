package com.sadxlab.notescompose.presentation

import com.sadxlab.notescompose.domain.model.Note

sealed class NoteUiState {
    object Loading : NoteUiState()
    data class Success(val note: Note) : NoteUiState()
    data class Error(val message: String) : NoteUiState()
}