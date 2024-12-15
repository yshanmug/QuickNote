package com.yuva.notetakingapp.di
import com.yuva.notetakingapp.repository.NoteRepository
import com.yuva.notetakingapp.repository.NoteRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideHomeRepository(noteRepositoryImpl: NoteRepositoryImp): NoteRepository
}


