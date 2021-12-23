package com.hyeeyoung.wishboard.repository.cart

import android.util.Log
import com.hyeeyoung.wishboard.model.CartItem
import com.hyeeyoung.wishboard.remote.RemoteService

class CartRepositoryImpl : CartRepository {
    private val api = RemoteService.api

    override suspend fun addToCart(token: String, itemId: Int): Boolean {
        val response = api.addToCart(token, itemId)
        if (response.isSuccessful) {
            Log.d(TAG, "장바구니 추가 성공")
        } else {
            Log.e(TAG, "장바구니 추가 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    override suspend fun fetchCart(token: String): ArrayList<CartItem>? {
        val response = api.fetchCart(token) ?: return null
        if (response.isSuccessful) {
            Log.d(TAG, "장바구니 가져오기 성공")
        } else {
            Log.e(TAG, "장바구니 가져오기 성공: ${response.code()}")
        }
        return response.body()
    }

    companion object {
        private const val TAG = "CartRepositoryImpl"
    }
}