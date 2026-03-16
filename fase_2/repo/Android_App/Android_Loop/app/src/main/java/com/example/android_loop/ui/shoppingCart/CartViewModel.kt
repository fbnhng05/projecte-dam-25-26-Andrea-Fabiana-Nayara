package com.example.android_loop.ui.shoppingCart

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.android_loop.data.model_dataClass.Producto

class CartViewModel : ViewModel() {

    val cartItems = mutableStateListOf<Producto>()

    val total: Double
        get() = cartItems.sumOf { it.precio }

    fun addToCart(product: Producto) {
        if (cartItems.none { it.id == product.id }) {
            cartItems.add(product)
        }
    }

    fun removeFromCart(product: Producto) {
        cartItems.removeAll { it.id == product.id }
    }

    fun clearCart() {
        cartItems.clear()
    }
}