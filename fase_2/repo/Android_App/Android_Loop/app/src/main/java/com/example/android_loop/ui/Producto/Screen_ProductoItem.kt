package com.example.android_loop.data.Producto

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_loop.data.model_dataClass.Producto
import com.tuapp.ui.theme.Primary

@Composable
fun ProductItem(
    product: Producto,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isFavorite: Boolean,           // NUEVO: ¿está en favoritos?
    onToggleFavorite: () -> Unit   // NUEVO: acción al pulsar el corazón
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            val bitmap = remember(product.thumbnail) {
                product.thumbnail?.let {
                    try {
                        val bytes = Base64.decode(it, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (e: Exception) { null }
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(115.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(115.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .background(Color(0xFFE8EEF4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sin imagen",
                        color = Color(0xFF8FA3B1),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    color = Color(0xFF1A1A2E)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "%.2f €".format(product.precio),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003459)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF8FA3B1),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = product.ubicacion,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8FA3B1),
                        maxLines = 1
                    )
                }
            }

            // ── Columna derecha: corazón + carrito ───────────────────
            // NUEVO: agrupamos los dos botones en una Column para que
            // queden uno encima del otro de forma ordenada.
            Row(
                modifier = Modifier.padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Icono favorito
                // - Filled = favorito activo (rojo)
                // - Border  = favorito inactivo (gris)
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos"
                        else "Añadir a favoritos",
                        tint = if (isFavorite) Color(0xFFE63946) else Color(0xFF8FA3B1),
                        modifier = Modifier.size(22.dp)
                    )
                }


                // Botón añadir al carrito (sin cambios, solo movido aquí)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF003459))
                        .clickable { onAddToCart() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir al carrito",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// CARD CUADRADO — estilo Wallapop
// Imagen cuadrada arriba, info abajo, corazón sobre la imagen,
// botón + en la esquina inferior derecha
// ──────────────────────────────────────────────────────────────
@Composable
fun ProductCardSquare(
    product: Producto,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val bitmap = remember(product.thumbnail) {
        product.thumbnail?.let {
            try {
                val bytes = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (e: Exception) { null }
        }
    }

    Card(
        modifier = Modifier
            .width(160.dp)         // Ancho fijo para que quepan ~2 cards visibles a la vez
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            // ── Imagen cuadrada + corazón encima ──────────────────────
            Box(modifier = Modifier.fillMaxWidth()) {

                // Imagen del producto (relación 1:1 = cuadrada)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)             // Fuerza formato cuadrado
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFFE8EEF4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin imagen", color = Color(0xFF8FA3B1),
                            style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Corazón en la esquina superior derecha, sobre la imagen
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .background(                         // Fondo semitransparente para que se lea
                            Color.White.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite
                                      else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color(0xFFE63946) else Color(0xFF8FA3B1),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // ── Info del producto ──────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {

                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1A1A2E)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "%.2f €".format(product.precio),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Ubicación + botón + en la misma fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF8FA3B1),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = product.ubicacion,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8FA3B1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón + añadir al carrito
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary)
                            .clickable { onAddToCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir al carrito",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
