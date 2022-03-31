package com.hyeeyoung.wishboard.repository.common

import android.content.ContentResolver
import android.net.Uri

interface GalleryRepository {
    suspend fun fetchGalleryImage(startPagingIndex: Int, contentResolver: ContentResolver): Pair<List<Uri>, Int>
}