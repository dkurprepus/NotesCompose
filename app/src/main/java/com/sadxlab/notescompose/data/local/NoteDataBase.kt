package com.sadxlab.notescompose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 2)
abstract class NoteDataBase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

}