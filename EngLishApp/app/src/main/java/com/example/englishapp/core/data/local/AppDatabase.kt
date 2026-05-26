package com.example.englishapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.StudySessionDao
import com.example.englishapp.core.data.local.dao.WordDao
import com.example.englishapp.core.data.local.dao.VocabularySetDao
import com.example.englishapp.core.data.local.dao.StreakDao
import com.example.englishapp.core.data.local.entity.SrsCardEntity
import com.example.englishapp.core.data.local.entity.StudySessionEntity
import com.example.englishapp.core.data.local.entity.WordEntity
import com.example.englishapp.core.data.local.entity.VocabularySetEntity
import com.example.englishapp.core.data.local.entity.StreakEntity

@Database(
    entities = [
        WordEntity::class,
        SrsCardEntity::class,
        StudySessionEntity::class,
        VocabularySetEntity::class,
        StreakEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun srsCardDao(): SrsCardDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun vocabularySetDao(): VocabularySetDao
    abstract fun streakDao(): StreakDao

    companion object {
        const val DATABASE_NAME = "minlish_db"
    }
}