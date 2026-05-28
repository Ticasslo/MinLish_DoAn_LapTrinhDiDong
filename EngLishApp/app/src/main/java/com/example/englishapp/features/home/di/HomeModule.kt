package com.example.englishapp.features.home.di

import com.example.englishapp.features.home.data.repository.HomeRepository
import com.example.englishapp.features.home.domain.repository.IHomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        repository: HomeRepository
    ): IHomeRepository
}