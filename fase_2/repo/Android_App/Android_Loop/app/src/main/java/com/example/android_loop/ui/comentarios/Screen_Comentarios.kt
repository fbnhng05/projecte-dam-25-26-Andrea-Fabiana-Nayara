package com.example.android_loop.ui.comentarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ComentarioBurbuja(comentario: Comentario, esMio: Boolean) {
    val bubbleColor = if (esMio) Color(0xFF003459) else Color(0xFFEEEEEE)
    val textColor = if (esMio) Color.White else Color.Black
    val alignment = if (esMio) Alignment.End else Alignment.Start
    val bubbleShape = if (esMio)
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    else
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!esMio) {
            Text(
                text = comentario.comentador,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF003459),
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }

        Card(
            shape = bubbleShape,
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = comentario.contenido,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comentario.fecha_creacion.take(10),
                    color = textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
