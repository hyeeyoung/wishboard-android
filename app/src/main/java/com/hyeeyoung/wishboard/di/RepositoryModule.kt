package com.hyeeyoung.wishboard.di

import com.hyeeyoung.wishboard.data.repositories.*
import com.hyeeyoung.wishboard.domain.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindSignRepository(signRepositoryImpl: SignRepositoryImpl): SignRepository

    @Binds
    abstract fun bindWishRepository(wishRepositoryImpl: WishRepositoryImpl): WishRepository

    @Binds
    abstract fun bindCartRepository(cartRepositoryImpl: CartRepositoryImpl): CartRepository

    @Binds
    abstract fun bindFolderRepository(folderRepositoryImpl: FolderRepositoryImpl): FolderRepository

    @Binds
    abstract fun bindGalleryRepository(galleryRepositoryImpl: GalleryRepositoryImpl): GalleryRepository

    @Binds
    abstract fun bindNotiRepository(notiRepositoryImpl: NotiRepositoryImpl): NotiRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindPhotoRepository(photoRepositoryImpl: PhotoRepositoryImpl): PhotoRepository

    @Binds
    abstract fun bindSystemRepository(systemRepositoryImpl: SystemRepositoryImpl): SystemRepository
}