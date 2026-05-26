package com.example.englishapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.englishapp.core.data.local.dao.*
import com.example.englishapp.core.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        WordEntity::class,
        SrsCardEntity::class,
        StudySessionEntity::class,
        VocabularySetEntity::class,
        StreakEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wordDao(): WordDao
    abstract fun srsCardDao(): SrsCardDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun vocabularySetDao(): VocabularySetDao
    abstract fun streakDao(): StreakDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DATABASE_NAME = "minlish_db"
    }
}
