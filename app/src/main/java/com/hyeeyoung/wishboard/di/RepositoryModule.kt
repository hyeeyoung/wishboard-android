package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.repository.cart.CartRepository
import com.hyeeyoung.wishboard.repository.cart.CartRepositoryImpl
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
}