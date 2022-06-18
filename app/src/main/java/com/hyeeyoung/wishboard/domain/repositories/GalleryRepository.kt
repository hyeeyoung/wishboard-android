package com.hyeeyoung.wishboard.domain.repositories

import android.content.ContentResolver
import android.net.Uri

interface GalleryRepository {
    suspend fun fetchGalleryImage(startPagingIndex: Int, contentResolver: ContentResolver): Pair<List<Uri>, Int>
}