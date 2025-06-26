package com.sadxlab.notescompose.domain.usecases

import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotes(private val repository: NoteRepository) {
    suspend operator fun invoke(): Flow<List<Note>> =repository.getNotes()
}