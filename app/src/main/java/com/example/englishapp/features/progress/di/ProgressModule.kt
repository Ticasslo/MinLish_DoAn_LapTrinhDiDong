package com.example.englishapp.features.progress.di

import com.example.englishapp.features.progress.data.repository.ProgressRepository
import com.example.englishapp.features.progress.domain.repository.IProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProgressModule {

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        repository: ProgressRepository
    ): IProgressRepository
}
