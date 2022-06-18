package com.hyeeyoung.wishboard.domain.di

import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import com.hyeeyoung.wishboard.data.repositories.CartRepositoryImpl
import com.hyeeyoung.wishboard.domain.repositories.GalleryRepository
import com.hyeeyoung.wishboard.data.repositories.GalleryRepositoryImpl
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.data.repositories.FolderRepositoryImpl
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.data.repositories.NotiRepositoryImpl
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.data.repositories.WishRepositoryImpl
import com.hyeeyoung.wishboard.domain.repositories.SignRepository
import com.hyeeyoung.wishboard.data.repositories.SignRepositoryImpl
import com.hyeeyoung.wishboard.domain.repositories.UserRepository
import com.hyeeyoung.wishboard.data.repositories.UserRepositoryImpl
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

    @Singleton
    @Provides
    fun provideWishRepository(): WishRepository {
        return WishRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideCartRepository(): CartRepository {
        return CartRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideFolderRepository(): FolderRepository {
        return FolderRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideGalleryRepository(): GalleryRepository {
        return GalleryRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideNotiRepository(): NotiRepository {
        return NotiRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }
}