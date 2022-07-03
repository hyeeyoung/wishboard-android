package com.hyeeyoung.wishboard.data.repositories

import android.content.Context
import com.hyeeyoung.wishboard.data.datasources.GalleryPagingDataSource
import com.hyeeyoung.wishboard.domain.repositories.GalleryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// TODO rename ~~Impl class name
class GalleryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GalleryRepository {
    override fun galleryPagingSource() = GalleryPagingDataSource(context)
}