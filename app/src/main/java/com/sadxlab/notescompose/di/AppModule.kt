package com.sadxlab.notescompose.di

import android.app.Application
import androidx.room.Room
import com.sadxlab.notescompose.data.local.DataBaseUtil.MIGRATION_1_2
import com.sadxlab.notescompose.data.local.NoteDao
import com.sadxlab.notescompose.data.local.NoteDataBase
import com.sadxlab.notescompose.data.repository.NoteRepositoryImpl
import com.sadxlab.notescompose.domain.repository.NoteRepository
import com.sadxlab.notescompose.domain.usecases.AddNote
import com.sadxlab.notescompose.domain.usecases.DeleteNote
import com.sadxlab.notescompose.domain.usecases.GetAllNotes
import com.sadxlab.notescompose.domain.usecases.GetNoteById
import com.sadxlab.notescompose.domain.usecases.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataBase(app: Application): NoteDataBase {
        return Room.databaseBuilder(
            app,
            NoteDataBase::class.java,
            "note_db"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideNoteDao(db: NoteDataBase): NoteDao = db.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository = NoteRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            addNote = AddNote(repository),
            getAllNotes = GetAllNotes(repository),
            deleteNote = DeleteNote(repository),
            getNoteById = GetNoteById(repository)
        )
    }


}