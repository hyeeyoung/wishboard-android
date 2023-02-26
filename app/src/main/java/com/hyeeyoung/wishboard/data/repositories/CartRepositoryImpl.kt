package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.data.services.retrofit.CartService
import com.hyeeyoung.wishboard.domain.repositories.CartRepository
import timber.log.Timber
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(private val cartService: CartService) :
    CartRepository {
    override suspend fun addToCart(itemId: Long): Boolean {
        try {
            val response = cartService.addToCart(itemId)
            if (response.isSuccessful) {
                Timber.d("장바구니에서 추가 성공")
            } else {
                Timber.e("장바구니에서 추가 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateCartItemCount(item: CartItem): Boolean {
        try {
            if (item.wishItem.id == null) return false
            val response =
                cartService.updateToCart(item.wishItem.id!!, item.cartItemInfo.count)
            if (response.isSuccessful) {
                Timber.d("장바구니 수정 성공")
            } else {
                Timber.e("장바구니 수정 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun removeToCart(itemId: Long): Boolean {
        try {
            val response = cartService.removeToCart(itemId)
            if (response.isSuccessful) {
                Timber.d("장바구니에서 제거 성공")
            } else {
                Timber.e("장바구니에서 제거 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun fetchCartList(): List<CartItem>? {
        try {
            val response = cartService.fetchCart() ?: return null
            if (response.isSuccessful) {
                Timber.d("장바구니 가져오기 성공")
            } else {
                Timber.e("장바구니 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}