package com.hyeeyoung.wishboard.repository.cart

import android.util.Log
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.remote.RemoteService

class CartRepositoryImpl : CartRepository {
    private val api = RemoteService.api

    override suspend fun addToCart(token: String, itemId: Int): Boolean {
        val response = api.addToCart(token, itemId)
        if (response.isSuccessful) {
            Log.d(TAG, "장바구니에 추가 성공")
        } else {
            Log.e(TAG, "장바구니에 추가 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    override suspend fun removeToCart(token: String, itemId: Int): Boolean {
        val response = api.removeToCart(token, itemId)
        if (response.isSuccessful) {
            Log.d(TAG, "장바구니에서 제거 성공")
        } else {
            Log.e(TAG, "장바구니에서 제거 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    override suspend fun fetchCartList(token: String): List<CartItem>? {
        val response = api.fetchCart(token) ?: return null
        if (response.isSuccessful) {
            Log.d(TAG, "장바구니 가져오기 성공")
        } else {
            Log.e(TAG, "장바구니 가져오기 실패: ${response.code()}")
        }
        return response.body()
    }

    companion object {
        private const val TAG = "CartRepositoryImpl"
    }
}