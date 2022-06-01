package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.service.RemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Singleton
    @Provides
    fun bindRetrofitService(): RemoteService =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(
            GsonConverterFactory.create()
        ).build().create(RemoteService::class.java)
}