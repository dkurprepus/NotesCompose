package com.sadxlab.notescompose.data.repository

import com.sadxlab.notescompose.data.local.NoteDao
import com.sadxlab.notescompose.data.local.mappers.toEntity
import com.sadxlab.notescompose.data.local.mappers.toNote
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository

class NoteRepositoryImpl(private val dao: NoteDao) : NoteRepository {
    override suspend fun addNote(note: Note) {
        dao.insert(note.toEntity())
    }

    override suspend fun getNotes(): List<Note> {
        return dao.getAll().map { it.toNote() }
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note = note.toEntity())
    }

    override suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)?.toNote()
    }
}