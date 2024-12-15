package com.yuva.notetakingapp.di

import android.app.Application
import androidx.room.Room
import com.yuva.notetakingapp.NoteRoomDataBase
import com.yuva.notetakingapp.NoteTakingDao
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
    fun provideNoteDatabase(app: Application): NoteRoomDataBase {
        return Room.databaseBuilder(
            app,
            NoteRoomDataBase::class.java,
            NoteRoomDataBase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteTakingDao(database: NoteRoomDataBase): NoteTakingDao {
        return database.noteDao()
    }
}

