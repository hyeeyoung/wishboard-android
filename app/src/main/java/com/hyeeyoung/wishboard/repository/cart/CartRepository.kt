package com.hyeeyoung.wishboard.repository.cart

import com.hyeeyoung.wishboard.model.cart.CartItem

interface CartRepository {
    suspend fun addToCart(token: String, itemId: Int): Boolean
    suspend fun removeToCart(token: String, itemId: Int): Boolean
    suspend fun fetchCartList(token: String): List<CartItem>?
}