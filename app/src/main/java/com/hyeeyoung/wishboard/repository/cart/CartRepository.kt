package com.hyeeyoung.wishboard.repository.cart

import com.hyeeyoung.wishboard.model.cart.CartItem

interface CartRepository {
    suspend fun addToCart(token: String, itemId: Int): Boolean
    suspend fun fetchCart(token: String): ArrayList<CartItem>?
}