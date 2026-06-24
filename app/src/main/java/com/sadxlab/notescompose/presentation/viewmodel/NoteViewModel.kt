package com.sadxlab.notescompose.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadxlab.notescompose.data.local.mappers.toEntity
import com.sadxlab.notescompose.domain.model.Note
import com.sadxlab.notescompose.domain.usecases.NoteUseCases
import com.sadxlab.notescompose.presentation.NoteUiState
import com.sadxlab.notescompose.presentation.SortOrder
import com.sadxlab.notescompose.presentation.UiEvent
import com.sadxlab.notescompose.presentation.ui.FirstLaunchManager
import com.sadxlab.notescompose.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val useCases: NoteUseCases,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val notes: StateFlow<List<Note>> = combine(_notes, _searchQuery, _sortOrder) { notes, query, order ->
        val filtered = if (query.isBlank()) notes
        else notes.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        val sorted = when (order) {
            SortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.timestamp }
            SortOrder.OLDEST_FIRST -> filtered.sortedBy { it.timestamp }
            SortOrder.A_TO_Z -> filtered.sortedBy { it.title.lowercase() }
        }
        sorted.sortedByDescending { it.isPinned }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingNote = MutableStateFlow<Note?>(null)
    val editingNote: StateFlow<Note?> = _editingNote.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Loading)
    val uiState: StateFlow<NoteUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val prefs = appContext.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private var isNoteSaving = false

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }
    fun toggleLayout() { _isGridView.value = !_isGridView.value }
    fun toggleDarkMode() {
        val new = !_isDarkMode.value
        _isDarkMode.value = new
        prefs.edit().putBoolean("dark_mode", new).apply()
    }

    fun loadNotes() {
        viewModelScope.launch {
            useCases.getAllNotes().collect {
                _notes.value = it
                _isLoading.value = false
            }
        }
    }

    fun addNote(note: Note) {
        Log.d("NoteViewModel", "addNote: ${note.toEntity()}")
        viewModelScope.launch {
            try {
                val insertedId = useCases.addNoteUseCase(note).toInt()
                val now = System.currentTimeMillis()
                note.reminder?.let { time ->
                    if (time > now) ReminderScheduler.schedule(appContext, insertedId, note.title, time)
                }
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
                // Always cancel the existing alarm first, then reschedule if still needed
                if (note.id != 0) ReminderScheduler.cancel(appContext, note.id)
                useCases.addNoteUseCase(note)
                val now = System.currentTimeMillis()
                note.reminder?.let { time ->
                    if (time > now) ReminderScheduler.schedule(appContext, note.id, note.title, time)
                }
                loadNotes()
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Failed to save note"))
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            ReminderScheduler.cancel(appContext, note.id)
            useCases.deleteNote(note)
            loadNotes()
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            val toggled = note.copy(isPinned = !note.isPinned)
            useCases.addNoteUseCase(toggled)
            _editingNote.value = toggled
            loadNotes()
        }
    }

    fun loadNoteById(id: Int) {
        viewModelScope.launch {
            _editingNote.value = useCases.getNoteById(id)
        }
    }

    fun insertPrefilledNotesIfFirstTime() {
        if (FirstLaunchManager.isFirstLaunch(appContext)) {
            viewModelScope.launch {
                val defaultNotes = listOf(
                    Note(
                        title = "Welcome to Only Notes!",
                        content = "Start capturing your thoughts, plans, or inspirations. Tap the + button to add a new note!",
                        color = 0xFFFFF59D.toInt(),
                        timestamp = System.currentTimeMillis()
                    ),
                    Note(
                        title = "Your Daily Motivation 💪",
                        content = "“Success doesn't come from what you do occasionally. It comes from what you do consistently.”",
                        color = 0xFFC8E6C9.toInt(),
                        timestamp = System.currentTimeMillis()
                    ),
                    Note(
                        title = "Morning Routine 🌅",
                        content = "1. Wake up at 6:30 AM\n2. Drink a glass of water\n3. 10 min meditation\n4. 30 min workout\n5. Healthy breakfast\n6. Plan the day",
                        color = 0xFFFFCDD2.toInt(),
                        timestamp = System.currentTimeMillis()
                    ),
                    Note(
                        title = "Weekly Gym Routine 🏋️",
                        content = "- Mon: Chest & Triceps\n- Tue: Back & Biceps\n- Wed: Rest or Cardio\n- Thu: Legs & Shoulders\n- Fri: Core + Full Body\n- Sat: Yoga or Stretch\n- Sun: Rest & Recover",
                        color = 0xFFD1C4E9.toInt(),
                        timestamp = System.currentTimeMillis()
                    ),
                    Note(
                        title = "Gratitude Practice 💛",
                        content = "Take a minute every day to note 3 things you're grateful for. It'll boost your mood and mindset!",
                        color = 0xFFFFE0B2.toInt(),
                        timestamp = System.currentTimeMillis()
                    ),
                    Note(
                        title = "Goals & Ideas 🎯",
                        content = "💡 Learn a new skill\n📝 Read one book a month\n🌍 Travel to 3 new places\n🤝 Help someone this week",
                        color = 0xFFF8BBD0.toInt(),
                        timestamp = System.currentTimeMillis()
                    )
                )
                defaultNotes.forEach { useCases.addNoteUseCase(it) }
                FirstLaunchManager.setFirstLaunchDone(appContext)
            }
        }
    }
}
