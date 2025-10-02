package com.sadxlab.notescompose.domain.usecases

data class NoteUseCases(
    val addNoteUseCase: AddNoteUseCase,
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote,
    val getNoteById: GetNoteById,
)