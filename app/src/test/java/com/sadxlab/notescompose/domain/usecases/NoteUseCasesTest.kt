package com.sadxlab.notescompose.domain.usecases

import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


@OptIn(ExperimentalCoroutinesApi::class)
class NoteUseCasesTest {
    private lateinit var repository: NoteRepository
    private lateinit var useCases: NoteUseCases

    @Before
    fun setup() {
        repository = mock()

        useCases = NoteUseCases(
            getAllNotes = GetAllNotes(repository),
            getNoteById = GetNoteById(repository),
            addNote = AddNote(repository),
            deleteNote = DeleteNote(repository),
        )

    }

    @Test
    fun `addNote should call repository`() = runTest {
        val testNote =
            Note(id = 1, title = "Test", content = "Hello Testing World", color = 0xFFFFFF)
        useCases.addNote(testNote)

        verify(repository).addNote(testNote)
    }

    @Test
    fun `delete note should call repository`() = runTest {
        val testNote =
            Note(id = 1, title = "Test", content = "Hello Testing World", color = 0xFFFFFF)

        useCases.deleteNote(testNote)

        verify(repository).deleteNote(testNote)
    }

    @Test
    fun `getNote by id note should call repository`() = runTest {
        val testNote =
            Note(id = 1, title = "Test", content = "Hello Testing World", color = 0xFFFFFF)

        useCases.getNoteById(testNote.id)

        verify(repository).getNoteById(testNote.id)
    }
    @Test
    fun `getAllNote note should call repository`() = runTest {


        useCases.getAllNotes()

        verify(repository).getNotes()
    }

}