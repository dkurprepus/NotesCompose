package com.sadxlab.notescompose.domain.usecases

import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository

class GetNoteById(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: Int) = repository.getNoteById(noteId)

}