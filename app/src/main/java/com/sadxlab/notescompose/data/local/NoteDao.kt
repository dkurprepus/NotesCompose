package com.sadxlab.notescompose.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Query("SELECT * FROM notes")
    suspend fun getAll(): List<NoteEntity>

    @Delete()
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id=:id")
    suspend fun getNoteById(id: Int): NoteEntity?
}