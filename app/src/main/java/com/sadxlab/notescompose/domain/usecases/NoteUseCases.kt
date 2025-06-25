package com.sadxlab.notescompose.domain.usecases

data class NoteUseCases(
    val addNote: AddNote,
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote,
    val getNoteById: GetNoteById,
)