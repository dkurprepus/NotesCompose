package com.sadxlab.notescompose.data.local.mappers

import com.sadxlab.notescompose.data.local.NoteEntity
import com.sadxlab.notescompose.domain.model.Note

// Converts NoteEntity (Room) to Note (Domain)
fun NoteEntity.toNote(): Note {
    return Note(id = id, title = title, content = content, color = color, timestamp = timestamp, isPinned = isPinned, reminder = reminder)
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(id = id, title = title, content = content, color = color, timestamp = timestamp, isPinned = isPinned, reminder = reminder)
}