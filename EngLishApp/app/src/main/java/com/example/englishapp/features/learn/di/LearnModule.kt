package com.example.englishapp.features.learn.di

import com.example.englishapp.features.learn.data.repository.LearnRepository
import com.example.englishapp.features.learn.domain.repository.ILearnRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LearnModule {

    @Binds
    @Singleton
    abstract fun bindLearnRepository(
        learnRepository: LearnRepository
    ): ILearnRepository
}
