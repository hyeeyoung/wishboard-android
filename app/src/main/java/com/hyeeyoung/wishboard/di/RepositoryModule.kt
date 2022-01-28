package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.cart.CartRepositoryImpl
import com.hyeeyoung.wishboard.repository.common.GalleryRepository
import com.hyeeyoung.wishboard.repository.common.GalleryRepositoryImpl
import com.hyeeyoung.wishboard.repository.folder.FolderRepository
import com.hyeeyoung.wishboard.repository.folder.FolderRepositoryImpl
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.repository.wish.WishRepositoryImpl
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
}