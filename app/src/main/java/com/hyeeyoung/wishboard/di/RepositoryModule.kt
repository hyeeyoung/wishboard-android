package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.repository.sign.SignRepository
import com.hyeeyoung.wishboard.repository.sign.SignRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideSignRepository(): SignRepository {
        return SignRepositoryImpl()
    }
}