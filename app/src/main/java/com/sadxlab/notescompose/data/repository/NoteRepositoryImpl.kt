package com.sadxlab.notescompose.data.repository

import com.sadxlab.notescompose.data.local.NoteDao
import com.sadxlab.notescompose.data.local.mappers.toEntity
import com.sadxlab.notescompose.data.local.mappers.toNote
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(private val dao: NoteDao) : NoteRepository {
    override suspend fun addNote(note: Note) {
        dao.insert(note.toEntity())
    }

    override suspend fun getNotes(): Flow<List<Note>> {
        return dao.getAll().map { list -> list.map { it.toNote() } }    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note = note.toEntity())
    }

    override suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)?.toNote()
    }
}