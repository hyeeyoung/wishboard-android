package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.cart.CartItem

interface CartRepository {
    suspend fun addToCart(itemId: Long): Boolean
    suspend fun removeToCart(itemId: Long): Boolean
    suspend fun updateCartItemCount(item: CartItem): Boolean
    suspend fun fetchCartList(): List<CartItem>?
}