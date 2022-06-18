package com.hyeeyoung.wishboard.data.repositories

import android.util.Log
import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import com.hyeeyoung.wishboard.data.services.RemoteService

class WishRepositoryImpl : WishRepository {
    private val api = RemoteService.api

    override suspend fun fetchWishList(token: String): List<WishItem>? {
        try {
            val response = api.fetchWishList(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 가져오기 성공")
            } else {
                Log.e(TAG, "아이템 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun fetchLatestWishItem(token: String): WishItem? {
        try {
            val response = api.fetchLatestWishItem(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "가장 최근 등록된 아이템 가져오기 성공")
            } else {
                Log.e(TAG, "가장 최근 등록된 아이템 가져오기 실패: ${response.code()}")
            }
            return response.body()?.get(0)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun uploadWishItem(token: String, wishItem: WishItem): Boolean {
        try {
            val response = api.uploadWishItem(token, wishItem)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 등록 성공")
            } else {
                Log.e(TAG, "아이템 등록 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateWishItem(token: String, itemId: Long, wishItem: WishItem): Boolean {
        try {
            val response = api.updateToWishItem(token, itemId, wishItem)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 수정 성공")
            } else {
                Log.e(TAG, "아이템 수정 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateFolderOfWishItem(
        token: String,
        itemId: Long,
        folderId: Long
    ): Boolean {
        try {
            val response = api.updateFolderOfItem(token, itemId, folderId)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 폴더 수정 성공")
            } else {
                Log.e(TAG, "아이템 폴더 수정 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun deleteWishItem(token: String, itemId: Long): Boolean {
        try {
            val response = api.deleteWishItem(token, itemId)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 삭제 성공")
            } else {
                Log.e(TAG, "아이템 삭제 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun getItemParsingInfo(site: String): ItemInfo? {
        return runCatching {
            api.getItemParsingInfo(site)
        }.fold({
            Log.d(TAG, "아이템 파싱 성공")
            it.body()?.data
        }, {
            Log.d(TAG, "아이템 파싱 실패: ${it.message}")
            null
        })
    }

    companion object {
        private const val TAG = "WishRepositoryImpl"
    }
}