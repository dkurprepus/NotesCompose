package com.sadxlab.notescompose.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadxlab.notescompose.data.local.mappers.toEntity
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.usecases.NoteUseCases
import com.sadxlab.notescompose.presentation.NoteUiState
import com.sadxlab.notescompose.presentation.UiEvent
import com.sadxlab.notescompose.presentation.ui.FirstLaunchManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val useCases: NoteUseCases
) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _editingNote = MutableStateFlow<Note?>(null)
    val editingNote: StateFlow<Note?> = _editingNote.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Loading)
    val uiState: StateFlow<NoteUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    private var isNoteSaving = false

    fun loadNotes() {
        viewModelScope.launch {
            useCases.getAllNotes().collect {
                _notes.value = it
                _isLoading.value = false
            }
        }
    }

    fun addNote(note: Note) {
        Log.d("addNote", "addNote: CALLED for " + note.toEntity())


        viewModelScope.launch {
            try {
                useCases.addNote(note)
                loadNotes()
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Failed to save note"))
            } finally {
                isNoteSaving = false
            }

        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                useCases.addNote(note)
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Failed to save note"))
            }
            useCases.addNote(note)
            loadNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            useCases.deleteNote(note)
            loadNotes()
        }
    }


    fun loadNoteById(id: Int) {
        viewModelScope.launch {
            _editingNote.value = useCases.getNoteById(id)
        }
    }

    fun insertPrefilledNotesIfFirstTime(context: Context) {
        if (FirstLaunchManager.isFirstLaunch(context)) {
            viewModelScope.launch {
                val defaultNotes = listOf(
                    Note(
                        title = "Welcome to Only Notes!",
                        content = "Start capturing your thoughts, plans, or inspirations. Tap the + button to add a new note!",
                        color = 0xFFFFF59D.toInt(), // Light Yellow
                        timestamp = System.currentTimeMillis()
                    ), Note(
                        title = "Your Daily Motivation 💪",
                        content = "“Success doesn’t come from what you do occasionally. It comes from what you do consistently.”",
                        color = 0xFFC8E6C9.toInt(), // Light Green
                        timestamp = System.currentTimeMillis()
                    ), Note(
                        title = "Morning Routine 🌅",
                        content = """
                                1. Wake up at 6:30 AM
                                2. Drink a glass of water
                                3. 10 min meditation
                                4. 30 min workout
                                5. Healthy breakfast
                                6. Plan the day
                                """.trimIndent(), color = 0xFFFFCDD2.toInt(), // Light Red/Pink
                        timestamp = System.currentTimeMillis()
                    ),
                    Note(
                        title = "Weekly Gym Routine 🏋️",
                        content = """
                            - Mon: Chest & Triceps
                            - Tue: Back & Biceps
                            - Wed: Rest or Cardio
                            - Thu: Legs & Shoulders
                            - Fri: Core + Full Body
                            - Sat: Yoga or Stretch
                            - Sun: Rest & Recover
                            """.trimIndent(),
                        color = 0xFFD1C4E9.toInt(), // Lavender
                        timestamp = System.currentTimeMillis()
                    ), Note(
                        title = "Gratitude Practice 💛",
                        content = "Take a minute every day to note 3 things you're grateful for. It'll boost your mood and mindset!",
                        color = 0xFFFFE0B2.toInt(), // Light Orange
                        timestamp = System.currentTimeMillis()
                    ), Note(
                        title = "Goals & Ideas 🎯",
                        content = "💡 Learn a new skill\n📝 Read one book a month\n🌍 Travel to 3 new places\n🤝 Help someone this week",
                        color = 0xFFF8BBD0.toInt(), // Light Pink
                        timestamp = System.currentTimeMillis()
                    )

                )

                defaultNotes.forEach { note ->
                    useCases.addNote(note)
                }
                FirstLaunchManager.setFirstLaunchDone(context)
            }
        }
    }
}