package com.example.englishapp.features.vocab.di

import com.example.englishapp.features.vocab.data.repository.DictionaryRepository
import com.example.englishapp.features.vocab.data.repository.VocabRepository
import com.example.englishapp.features.vocab.domain.repository.IDictionaryRepository
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Cấu hình Dagger Hilt Module để liên kết (bind) các interface Repository
 * với lớp triển khai thực tế tương ứng.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class VocabModule {

    @Binds
    @Singleton
    abstract fun bindVocabRepository(
        vocabRepository: VocabRepository
    ): IVocabRepository

    @Binds
    @Singleton
    abstract fun bindDictionaryRepository(
        dictionaryRepository: DictionaryRepository
    ): IDictionaryRepository
}