package com.example.android_loop.ui.comentarios

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.example.android_loop.R

@Composable
fun ComentarioBurbuja(
    comentario: Comentario,
    esMio: Boolean,
    onPerfilClick: ((Int, String) -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onReport: (() -> Unit)? = null
) {
    val cardColor = Color(0xFFEEEEEE)
    val textColor = Color.Black
    val defaultAvatar = ImageBitmap.imageResource(R.drawable.no_avatar)

    val avatarBitmap = remember(comentario.imagen_comentador) {
        comentario.imagen_comentador
            ?.takeIf { it.isNotBlank() && it != "false" }
            ?.let {
                try {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } catch (e: Exception) { null }
            }
    } ?: defaultAvatar

    val clickableModifier: Modifier = if (onPerfilClick != null && comentario.comentador_partner_id != null)
        Modifier.clickable { onPerfilClick(comentario.comentador_partner_id, comentario.comentador) }
    else Modifier

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Cabecera: foto + nombre a la izquierda, botones de acción a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = avatarBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(36.dp).clip(CircleShape).then(clickableModifier)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = comentario.comentador,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = clickableModifier
                    )
                }
                Row {
                    if (esMio) {
                        IconButton(
                            onClick = { onDelete?.invoke() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar reseña",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                        }
                        IconButton(
                            onClick = { onEdit?.invoke() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar reseña",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onReport?.invoke() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Denunciar",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = comentario.contenido,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pie: fecha (izquierda) + estrellas (derecha)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comentario.fecha_creacion.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.5f)
                )
                if (comentario.valoracion != null) {
                    Row {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (star <= comentario.valoracion) Color(0xFFFFB800)
                                       else textColor.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
