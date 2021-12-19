package com.hyeeyoung.wishboard.repository.home

import android.util.Log
import com.hyeeyoung.wishboard.model.WishItem
import com.hyeeyoung.wishboard.model.WishItemInfo
import com.hyeeyoung.wishboard.remote.RemoteService

class WishRepositoryImpl : WishRepository {
    private val api = RemoteService.api

    override suspend fun fetchItems(token: String): List<WishItem>? {
        val response = api.fetchItems(token) ?: return null

        if (response.isSuccessful) {
            Log.d(TAG, "아이템 가져오기 성공")
        } else {
            Log.e(TAG, "아이템 가져오기 실패: ${response.code()}")
        }
        return response.body()
    }

    override suspend fun fetchItemDetail(token: String, itemId: Int): WishItemInfo? {
        val response = api.fetchItemDetail(token, itemId) ?: return null

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