package com.hyeeyoung.wishboard.data.repositories

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.hyeeyoung.wishboard.domain.repositories.GalleryRepository
import kotlin.math.min

// TODO rename ~~Impl class name
class GalleryRepositoryImpl : GalleryRepository {
    private var imageUris = mutableListOf<Uri>()
    private var pageIndex: Int = 0

    override suspend fun fetchGalleryImage(
        startPagingIndex: Int,
        contentResolver: ContentResolver
    ): Pair<List<Uri>, Int> {
        if (startPagingIndex == 0) {
            val uriExternal =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, sortOrder
            )?.use { cursor ->
                var imageId: Long
                while (cursor.moveToNext()) {
                    imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val uriImage = Uri.withAppendedPath(uriExternal, "" + imageId)
                    imageUris.add(uriImage)
                }
                cursor.close()
            }

            pageIndex = PAGE_SIZE
            return Pair(imageUris.subList(0, min(pageIndex, imageUris.size)), min(pageIndex, imageUris.size))
        } else {
            val startIndex = pageIndex
            pageIndex += PAGE_SIZE
            return Pair(imageUris.subList(startIndex, min(pageIndex, imageUris.size)), min(pageIndex, imageUris.size))
        }
    }
    companion object {
        private const val TAG = "GalleryRepositoryImpl"
        private const val PAGE_SIZE = 10
    }
}