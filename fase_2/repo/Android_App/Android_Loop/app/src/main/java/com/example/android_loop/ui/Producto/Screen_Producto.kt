package com.example.android_loop.ui.Producto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.android_loop.data.Producto.ProductItem
import com.example.android_loop.ui.shoppingCart.CartViewModel

@Composable
fun ProductScreen(
    token: String,
    viewModel: ViewModel_Producto,
    navController: NavController,
    cartViewModel: CartViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadProducts(token)
    }

    val products = viewModel.products
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.nombre.contains(searchQuery, ignoreCase = true) ||
                    it.categoria.nombre.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = { /* tu TopAppBar */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // tu buscador

            when {
                isLoading -> { CircularProgressIndicator(color = Color(0xFF003459)) }
                error != null -> { Text("Error al cargar los productos") }
                filteredProducts.isEmpty() && searchQuery.isNotBlank() -> { Text("No se encontraron productos") }
                else -> {
                    LazyColumn {
                        items(filteredProducts) { product ->
                            ProductItem(
                                product = product,
                                onClick = { navController.navigate("detalle_producto/${product.id}") },
                                onAddToCart = { cartViewModel.addToCart(product) },
                                isFavorite = viewModel.favoritoIds.contains(product.id),      // ← nuevo
                                onToggleFavorite = { viewModel.añadirOquitarfavorito(product.id) }   // ← nuevo
                            )
                        }
                    }
                }
            }
        }
    }
}