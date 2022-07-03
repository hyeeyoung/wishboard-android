package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.cart.CartItem

interface CartRepository {
    suspend fun addToCart(token: String, itemId: Long): Boolean
    suspend fun removeToCart(token: String, itemId: Long): Boolean
    suspend fun updateCartItemCount(token: String, item: CartItem): Boolean
    suspend fun fetchCartList(token: String): List<CartItem>?
}