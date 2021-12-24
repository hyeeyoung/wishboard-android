package com.hyeeyoung.wishboard.repository.wish

import android.util.Log
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemInfo
import com.hyeeyoung.wishboard.remote.RemoteService

class WishRepositoryImpl : WishRepository {
    private val api = RemoteService.api

    override suspend fun fetchWishList(token: String): List<WishItem>? {
        val response = api.fetchWishList(token) ?: return null

        if (response.isSuccessful) {
            Log.d(TAG, "아이템 가져오기 성공")
        } else {
            Log.e(TAG, "아이템 가져오기 실패: ${response.code()}")
        }
        return response.body()
    }

    override suspend fun fetchWishItem(token: String, itemId: Int): WishItemInfo? {
        val response = api.fetchWishItem(token, itemId) ?: return null

        if (response.isSuccessful) {
            Log.d(TAG, "아이템 상세정보 가져오기 성공")
        } else {
            Log.e(TAG, "아이템 상세정보 가져오기 실패: ${response.code()}")
        }
        return response.body()?.get(0)
    }

    companion object {
        private const val TAG = "WishRepositoryImpl"
    }
}