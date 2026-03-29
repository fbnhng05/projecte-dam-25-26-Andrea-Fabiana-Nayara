package com.example.android_loop.ui.Producto

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import android.net.Uri // necesario para las reseñas
import androidx.navigation.NavController
import com.example.android_loop.data.Producto.ImagenConDatos
import com.example.android_loop.ui.shoppingCart.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    token: String,
    productId: Int,
    viewModel: ViewModel_Producto,
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val product = viewModel.products.find { it.id == productId }

    LaunchedEffect(productId) {
        viewModel.loadProductImages(token, productId)
    }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Producto no encontrado")
        }
        return
    }

    val imagenes = viewModel.productImages

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.nombre) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            ImageCarousel(imagenes = imagenes)

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "%.2f €".format(product.precio),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Estado", product.estado)
                InfoRow("Ubicación", product.ubicacion)
                InfoRow("Categoría", product.categoria.nombre)
                InfoRow("Vendedor", product.propietario.nombre)
                product.antiguedad?.let { InfoRow("Antigüedad", it) }

                if (product.etiquetas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Etiquetas",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        product.etiquetas.forEach { etiqueta ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(etiqueta.nombre) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── INICIO reseñas ──
                OutlinedButton(
                    onClick = {
                        val nombreEncoded = Uri.encode(product.propietario.nombre)
                        navController.navigate("perfilVendedor/${product.propietario.id}/$nombreEncoded")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver perfil del vendedor")
                }
                // ── FIN reseñas ──

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        cartViewModel.addToCart(product)
                        navController.navigate("carrito")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir al carrito")
                }

            }
        }
    }
}

@Composable
private fun ImageCarousel(imagenes: List<ImagenConDatos>) {

    if (imagenes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val ordenadas = remember(imagenes) {
        imagenes.sortedWith(
            compareByDescending<ImagenConDatos> { it.is_principal }.thenBy { it.sequence }
        )
    }

    val pagerState = rememberPagerState(pageCount = { ordenadas.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val img = ordenadas[page]
            val bitmap = remember(img.id) {
                img.imagen?.let {
                    try {
                        val bytes = Base64.decode(it, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin imagen", color = Color.Gray)
                }
            }
        }

        // Indicadores de página (puntos)
        if (ordenadas.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(ordenadas.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) Color.White
                                else Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
