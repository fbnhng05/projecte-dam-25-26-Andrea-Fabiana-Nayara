package com.example.android_loop.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuapp.ui.theme.OnPrimary
import com.tuapp.ui.theme.Primary
import com.tuapp.ui.theme.Secondary

// Botón reutilizable con el estilo visual de la app Loop.
// Parámetros:
//   texto    -> lo que aparece escrito en el botón
//   onClick  -> la acción que se ejecuta al pulsarlo
//   modifier -> permite al que lo use ajustar el tamaño o margen desde fuera
//
// Ejemplo de uso: LoopBoton(texto = "Guardar Producto", onClick = { ... })
@Composable
fun LoopBoton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier  // Modifier por defecto vacío, se puede personalizar al usarlo
) {
    val forma = RoundedCornerShape(50.dp) // Forma de "píldora" — esquinas muy redondeadas

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(elevation = 6.dp, shape = forma),
        shape = forma,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        // Box interior que lleva el degradado de fondo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    // Degradado de izquierda a derecha igual que el PantallaHeader
                    brush = Brush.horizontalGradient(
                        colors = listOf(Primary, Secondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                color = OnPrimary
            )
        }
    }
}
