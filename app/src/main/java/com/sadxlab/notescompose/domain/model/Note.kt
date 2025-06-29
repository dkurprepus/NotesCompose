package com.sadxlab.notescompose.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.sadxlab.notescompose.data.local.NoteEntity

data class Note(
    val id: Int = 0,
    val title: String,
    val content: String,
    val color: Int = Color(0xFFFFF59D).toArgb(),
    val timestamp: Long = System.currentTimeMillis()


)
