package com.hyeeyoung.wishboard.repository.common

import android.content.ContentResolver
import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState

// TODO move another package
class GalleryPagingDataSource(
    private val contentResolver: ContentResolver,
    private val galleryRepository: GalleryRepository
) : PagingSource<Int, Uri>() {
    private var startIndex = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        return try {
            val nextPage = params.key ?: 1
            val response = galleryRepository.fetchGalleryImage(startIndex, contentResolver)
            startIndex = response.second
            LoadResult.Page(
                data = response.first,
                prevKey = null,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val TAG = "GalleryPagingDataSource"
    }
}