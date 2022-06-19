package com.hyeeyoung.wishboard.data.repositories

import android.util.Log
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.data.services.retrofit.CartService
import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(private val cartService: CartService) :
    CartRepository {
    override suspend fun addToCart(token: String, itemId: Long): Boolean {
        try {
            val response = cartService.addToCart(token, itemId)
            if (response.isSuccessful) {
                Log.d(TAG, "장바구니에서 추가 성공")
            } else {
                Log.e(TAG, "장바구니에서 추가 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateCartItemCount(token: String, item: CartItem): Boolean {
        try {
            if (item.wishItem.id == null) return false
            val response = cartService.updateToCart(token, item.wishItem.id!!, item.cartItemInfo.count)
            if (response.isSuccessful) {
                Log.d(TAG, "장바구니 수정 성공")
            } else {
                Log.e(TAG, "장바구니 수정 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun removeToCart(token: String, itemId: Long): Boolean {
        try {
            val response = cartService.removeToCart(token, itemId)
            if (response.isSuccessful) {
                Log.d(TAG, "장바구니에서 제거 성공")
            } else {
                Log.e(TAG, "장바구니에서 제거 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun fetchCartList(token: String): List<CartItem>? {
        try {
            val response = cartService.fetchCart(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "장바구니 가져오기 성공")
            } else {
                Log.e(TAG, "장바구니 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        private const val TAG = "CartRepositoryImpl"
    }
}