package com.sadxlab.notescompose.domain.repository

import com.sadxlab.notescompose.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun addNote(note: Note)
    suspend fun getNotes(): Flow<List<Note>>
    suspend fun deleteNote(note: Note)
    suspend fun getNoteById(id: Int): Note?
}