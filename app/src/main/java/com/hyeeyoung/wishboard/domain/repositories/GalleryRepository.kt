package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.datasources.GalleryPagingDataSource

interface GalleryRepository {
    fun galleryPagingSource(): GalleryPagingDataSource
}