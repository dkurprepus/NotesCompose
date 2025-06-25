package com.sadxlab.notescompose.domain.repository

import com.sadxlab.notescompose.domain.model.Note

interface NoteRepository {
    suspend fun addNote(note: Note)
    suspend fun getNotes(): List<Note>
    suspend fun deleteNote(note: Note)
    suspend fun getNoteById(id: Int): Note?
}