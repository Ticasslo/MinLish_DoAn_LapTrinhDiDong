package com.example.englishapp.features.notification.di

import com.example.englishapp.features.notification.data.repository.NotificationRepository
import com.example.englishapp.features.notification.domain.repository.INotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepository: NotificationRepository
    ): INotificationRepository
}