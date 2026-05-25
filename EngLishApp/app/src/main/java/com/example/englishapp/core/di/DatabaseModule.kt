package com.example.englishapp.core.di

import android.content.Context
import androidx.room.Room
import com.example.englishapp.core.data.local.AppDatabase
import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.StudySessionDao
import com.example.englishapp.core.data.local.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()

    @Provides
    fun provideSrsCardDao(db: AppDatabase): SrsCardDao = db.srsCardDao()

    @Provides
    fun provideStudySessionDao(db: AppDatabase): StudySessionDao = db.studySessionDao()
}