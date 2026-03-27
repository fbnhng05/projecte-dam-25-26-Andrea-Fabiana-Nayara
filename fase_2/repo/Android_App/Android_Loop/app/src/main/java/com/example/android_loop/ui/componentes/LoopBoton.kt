package com.example.android_loop.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuapp.ui.theme.Primary

// Botón reutilizable con estilo outlined (solo borde, sin relleno).
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
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(50.dp) // Forma de "píldora" — esquinas muy redondeadas

    // OutlinedButton ya tiene fondo transparente y texto Primary por defecto en Material3.
    // No necesitamos el parámetro "colors" — así evitamos el conflicto con el import de TV.
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),    // Altura fija para que todos los botones sean iguales
        shape = forma,
        border = BorderStroke(
            width = 2.dp,      // Grosor del borde
            color = Primary    // Color del borde usando nuestra paleta
        )
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge, // Tamaño de texto apropiado para botones
            color = Primary                              // Texto del mismo color que el borde
        )
    }
}
