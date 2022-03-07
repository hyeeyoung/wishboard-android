package com.hyeeyoung.wishboard.repository.wish

import android.util.Log
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.remote.RemoteService

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

    companion object {
        private const val TAG = "WishRepositoryImpl"
    }
}