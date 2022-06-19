package com.hyeeyoung.wishboard.domain.di

import com.hyeeyoung.wishboard.data.services.retrofit.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ServiceModule {
    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideWishItemService(retrofit: Retrofit): WishItemService =
        retrofit.create(WishItemService::class.java)

    @Singleton
    @Provides
    fun provideCartService(retrofit: Retrofit): CartService =
        retrofit.create(CartService::class.java)

    @Singleton
    @Provides
    fun provideFolderService(retrofit: Retrofit): FolderService =
        retrofit.create(FolderService::class.java)

    @Singleton
    @Provides
    fun provideNotiService(retrofit: Retrofit): NotiService =
        retrofit.create(NotiService::class.java)

    @Singleton
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)
}