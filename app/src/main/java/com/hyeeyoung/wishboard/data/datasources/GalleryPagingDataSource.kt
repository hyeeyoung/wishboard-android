package com.hyeeyoung.wishboard.data.datasources

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hyeeyoung.wishboard.data.model.GalleryData
import timber.log.Timber
import kotlin.math.max

// TODO move another package
class GalleryPagingDataSource(context: Context) :
    PagingSource<Int, GalleryData>() {
    private val cursor: Cursor?

    init {
        val contentUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        cursor = context.contentResolver.query(
            contentUri,
            projection, null, null, sortOrder
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryData> {
        return try {
            registerInvalidatedCallback {
                closeCursor()
            }

            val key = params.key ?: 0
            val imageList = fetchGalleryImage(key, params.loadSize)
            val prevKey = if (key == 0) null else max(key - params.loadSize, 0)
            val nextKey = if (imageList.isEmpty()) null else key + params.loadSize

            LoadResult.Page(
                data = imageList,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun fetchGalleryImage(key: Int, loadSize: Int): List<GalleryData> {
        val imageList = mutableListOf<GalleryData>()
        cursor?.let { cursor ->
            repeat(loadSize) { index ->
                if (cursor.moveToPosition(key + index)) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    imageList.add(GalleryData(id, contentUri))
                } else {
                    return@repeat
                }
            }
        }
        return imageList
    }

    private fun closeCursor() {
        Timber.d("closeCursor")
        if (cursor?.isClosed == false) {
            cursor.close()
        }
    }

    companion object {
        private const val TAG = "GalleryPagingDataSource"
    }
}