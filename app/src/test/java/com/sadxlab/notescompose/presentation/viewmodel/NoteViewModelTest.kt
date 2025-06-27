package com.sadxlab.notescompose.presentation.viewmodel

import com.sadxlab.notescompose.data.local.mappers.toEntity
import com.sadxlab.notescompose.data.local.mappers.toNote
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.repository.NoteRepository
import com.sadxlab.notescompose.domain.usecases.AddNote
import com.sadxlab.notescompose.domain.usecases.DeleteNote
import com.sadxlab.notescompose.domain.usecases.GetAllNotes
import com.sadxlab.notescompose.domain.usecases.GetNoteById
import com.sadxlab.notescompose.domain.usecases.NoteUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {
    private lateinit var repository: NoteRepository
    private lateinit var useCases: NoteUseCases
    private lateinit var viewModel: NoteViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        //  repository = mock(NoteRepository::class.java) under the hood it will assume mock as NoteRepository (Java Declaration)
        repository = mock()

        useCases = NoteUseCases(
            addNote = AddNote(repository),
            getAllNotes = GetAllNotes(repository),
            deleteNote = DeleteNote(repository),
            getNoteById = GetNoteById(repository),
        )
        viewModel = NoteViewModel(useCases)

    }

    @Test
    fun `loadNotes should emit all expected notes`() = runTest {
        val noteList = listOf(
            Note(1, "Test Title", "Test Content", 0xFFFFFF),
            Note(2, "Another Title", "More Content", 0xFFFFFF)
        )

        `when`(repository.getNotes()).thenReturn(flowOf(noteList))

        //when
        viewModel.loadNotes()

        advanceUntilIdle()
        //Then
        val result = viewModel.notes.first()

        assertEquals(noteList, result)


    }

    @Test
    fun `fetch single note By Id`() = runTest {

        val testNote = Note(id = 1, title = "Test note", content = "Test content", color = 0xFFFFFF)
        // Mock the repository to return a Flow of that note
        `when`(repository.getNoteById(1)).thenReturn((testNote))

        // Trigger ViewModel call
        viewModel.loadNoteById(1)
        advanceUntilIdle()

        val result = viewModel.editingNote.first()
        assertEquals(testNote, result)


    }

    @Test
    fun `delete note`() = runTest {

        val testNote = Note(id = 1, title = "Test", content = "Content")

        // Mock getNotes() to prevent crash
        whenever(repository.getNotes()).thenReturn(flowOf(listOf(testNote)))

        // Mock deleteNote() if needed
        whenever(repository.deleteNote(testNote)).thenReturn(Unit)

        viewModel.deleteNote(testNote)

        advanceUntilIdle() // let all coroutines finish

        // Verify if deleteNote or state updated
        verify(repository).deleteNote(testNote)


    }

    @Test
    fun `add Note`() = runTest {

        val testNote = Note(id = 1, title = "Title", content = "Content")
        whenever(repository.getNotes()).thenReturn(flowOf(listOf(testNote)))
        whenever(repository.addNote(testNote)).thenReturn(Unit)

        viewModel.addNote(testNote)

        advanceUntilIdle()

        verify(repository).addNote(testNote)

    }

    @Test
    fun `update Note`() = runTest {

        val testNote = Note(id = 1, title = "Title", content = "Content")
        whenever(repository.getNotes()).thenReturn(flowOf(listOf(testNote)))
        whenever(repository.addNote(testNote)).thenReturn(Unit)

        viewModel.updateNote(testNote)

        advanceUntilIdle()

        verify(repository).addNote(testNote)

        val result  =viewModel.notes.first()
        assertEquals(listOf(testNote),result)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}