package com.example.android_loop.data.Producto

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.android_loop.R
import com.example.android_loop.ui.Producto.ViewModel_Producto
import com.example.android_loop.ui.shoppingCart.CartViewModel
import com.tuapp.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ViewModel_Producto,
    navController: NavHostController,
    cartViewModel: CartViewModel
) {

    // Obtenemos el token guardado en SharedPreferences al iniciar sesión
    val context = LocalContext.current
    val token = context.getSharedPreferences("loop_prefs", MODE_PRIVATE)
        .getString("token", "") ?: ""

    val products = viewModel.products
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val cartCount = cartViewModel.cartItems.size

    // Texto que escribe el usuario en el buscador
    var searchQuery by remember { mutableStateOf("") }

    // Categoría seleccionada mediante el chip (null = mostrar todas)
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    // Cargamos productos al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadProducts(token)
    }

    // Filtramos por búsqueda (nombre o categoría)
    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.nombre.contains(searchQuery, ignoreCase = true) ||
                    it.categoria.nombre.contains(searchQuery, ignoreCase = true)
        }
    }

    // Lista de categorías únicas extraídas de los productos filtrados
    val categoriasUnicas = remember(filteredProducts) {
        filteredProducts.map { it.categoria.nombre }.distinct()
    }

    // Agrupamos los productos por categoría, aplicando también el filtro de chip
    val productosPorCategoria = remember(filteredProducts, categoriaSeleccionada) {
        val listaFiltrada = if (categoriaSeleccionada != null) {
            filteredProducts.filter { it.categoria.nombre == categoriaSeleccionada }
        } else {
            filteredProducts
        }
        listaFiltrada.groupBy { it.categoria.nombre } // Mapa: categoría → lista de productos
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // windowInsets(0) elimina el espacio azul extra que aparece bajo la barra de estado
                windowInsets = WindowInsets(0),
                title = {
                    Text(
                        "Tienda",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {

                    // Icono del carrito con badge que muestra la cantidad de artículos
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge { Text("$cartCount") }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito"
                            )
                        }
                    }

                    // Icono de ajustes
                    IconButton(onClick = { navController.navigate("ajustes") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Respetamos el espacio del TopAppBar
        ) {

            // ── Barra de búsqueda ──────────────────────────────────────────
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar productos...") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.lupa),
                        contentDescription = "Buscar",
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F4F8),
                    unfocusedContainerColor = Color(0xFFF0F4F8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Chips de categorías (scroll horizontal) ────────────────────
            // Cada chip representa una categoría; al pulsar filtra los productos
            if (categoriasUnicas.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip "Todos" para quitar el filtro de categoría
                    item {
                        FilterChip(
                            selected = categoriaSeleccionada == null,
                            onClick = { categoriaSeleccionada = null },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    // Un chip por cada categoría única
                    items(categoriasUnicas) { categoria ->
                        FilterChip(
                            selected = categoriaSeleccionada == categoria,
                            onClick = {
                                // Si ya estaba seleccionada, la deseleccionamos (volver a "Todos")
                                categoriaSeleccionada = if (categoriaSeleccionada == categoria) null else categoria
                            },
                            label = { Text(categoria) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Contenido principal ────────────────────────────────────────
            when {

                // Mostramos un spinner mientras cargamos los productos
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                // Error de red o servidor
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error al cargar los productos",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Sin resultados en la búsqueda
                filteredProducts.isEmpty() && searchQuery.isNotBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron productos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                // Lista de productos agrupados por categoría (estilo Wallapop)
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Por cada categoría creamos una sección con su título y un LazyRow
                        productosPorCategoria.forEach { (categoria, productosDeCategoria) ->

                            item {
                                Column {

                                    // ── Título de la sección (nombre de la categoría) ──
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = categoria,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A1A2E)
                                        )
                                        // Contador de productos en la sección
                                        Text(
                                            text = "${productosDeCategoria.size} artículos",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF8FA3B1)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // ── Fila horizontal de cards cuadradas ─────────────
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(productosDeCategoria) { product ->
                                            // Usamos el nuevo card cuadrado estilo Wallapop
                                            ProductCardSquare(
                                                product = product,
                                                onClick = {
                                                    navController.navigate("detalle_producto/${product.id}")
                                                },
                                                onAddToCart = {
                                                    cartViewModel.addToCart(product)
                                                },
                                                isFavorite = viewModel.favoritoIds.contains(product.id),
                                                onToggleFavorite = {
                                                    viewModel.añadirOquitarfavorito(product.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
