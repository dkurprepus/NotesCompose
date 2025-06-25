package com.sadxlab.notescompose.domain.usecases

import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository

class DeleteNote(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}