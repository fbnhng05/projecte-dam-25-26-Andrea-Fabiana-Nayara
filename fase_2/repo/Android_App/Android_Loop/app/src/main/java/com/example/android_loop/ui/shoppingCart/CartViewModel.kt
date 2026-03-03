package com.example.android_loop.ui.shoppingCart

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.android_loop.data.Producto.Product

class CartViewModel : ViewModel() {

    val cartItems = mutableStateListOf<Product>()

    val total: Double
        get() = cartItems.sumOf { it.precio }

    fun addToCart(product: Product) {
        if (cartItems.none { it.id == product.id }) {
            cartItems.add(product)
        }
    }

    fun removeFromCart(product: Product) {
        cartItems.removeAll { it.id == product.id }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
