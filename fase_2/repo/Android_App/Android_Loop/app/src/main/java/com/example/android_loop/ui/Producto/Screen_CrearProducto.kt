package com.example.android_loop.data.Producto

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip         // Permite recortar un composable con una forma (ej. esquinas redondeadas)
import androidx.compose.ui.layout.ContentScale  // Define cómo se escala/recorta una imagen dentro de su espacio
// Brush ya no se necesita aquí, se usa dentro de PantallaHeader
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.android_loop.ui.Producto.ViewModel_Producto
import com.tuapp.ui.theme.Primary    // Color azul oscuro de nuestra paleta (#003459)
import com.tuapp.ui.theme.Secondary  // Color azul océano de nuestra paleta (#007EA7)
// OnPrimary ya no se necesita aquí, se usa dentro de PantallaHeader
import com.example.android_loop.ui.componentes.PantallaHeader  // Nuestro header reutilizable
import com.example.android_loop.ui.componentes.LoopBoton       // Nuestro botón reutilizable
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: ViewModel_Producto,
    navController: NavController
) {


    val context = LocalContext.current

    // Recuperamos el token guardado en el login
    val prefs = context.getSharedPreferences("loop_prefs", MODE_PRIVATE)
    val token = prefs.getString("token", "") ?: ""

    val scrollState = rememberScrollState()

    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var ubicacion by rememberSaveable { mutableStateOf("") }
    val selectedEtiquetas = rememberSaveable { mutableStateListOf<Int>() }

    var estado by rememberSaveable { mutableStateOf("nuevo") }
    val estados = listOf("nuevo", "segunda_mano", "reacondicionado")

    var categoriaId by rememberSaveable { mutableStateOf(1) }
    val categorias = listOf(
        1 to "Electrónica",
        2 to "Ropa",
        3 to "Hogar"
    )

    val imageUris = remember { mutableStateListOf<Uri>() }

    var selectedDate by rememberSaveable { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        // addAll añade las nuevas URIs a la lista sin borrar las que ya había
        // Antes había un imageUris.clear() aquí que las eliminaba cada vez
        imageUris.addAll(uris)
    }

    LaunchedEffect(viewModel.productCreated) {
        if (viewModel.productCreated) {

            Toast.makeText(
                context,
                "Producto creado correctamente",
                Toast.LENGTH_SHORT
            ).show()

            navController.navigate("pantalla_listado") {
                popUpTo("crear_producto") { inclusive = true }
            }

            viewModel.resetProductCreated()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadEtiquetas(token)
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        selectedDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Column exterior SIN padding para que el header llegue a los bordes
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {

        // El header va aquí fuera del padding, a tope con los bordes de la pantalla
        PantallaHeader(titulo = "Crear Producto")

        // Column interior CON padding para el resto del contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(imageUris){ uri ->

                Box(
                    modifier = Modifier.size(120.dp)
                ) {

                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop, // Recorta la imagen para que llene el cuadrado sin deformarse
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)) // Aplica esquinas redondeadas igual que el botón "Subir fotos"
                    )

                    // Botón eliminar
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                            .clickable {
                                imageUris.remove(uri)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Subir fotos")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Colores personalizados para todos los OutlinedTextField
        // Se define una sola vez y se reutiliza en cada campo para no repetir código
        val campoColores = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Secondary,      // Borde cuando el campo está activo (al hacer click)
            unfocusedBorderColor = Primary,      // Borde cuando el campo no está activo
            focusedLabelColor = Secondary,       // Label (ej. "Nombre") cuando el campo está activo
            unfocusedLabelColor = Primary,       // Label cuando el campo no está activo
            cursorColor = Secondary,             // Color del cursor de escritura
            // Los campos "disabled" (como la fecha) también aplican estos colores
            disabledBorderColor = Primary,       // Borde cuando el campo está deshabilitado
            disabledLabelColor = Primary         // Label cuando el campo está deshabilitado
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = campoColores  // Aplicamos los colores definidos arriba
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            colors = campoColores
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            colors = campoColores
        )

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth(),
            colors = campoColores
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ESTADO DROPDOWN
        var expandedEstado by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedEstado,
            onExpandedChange = { expandedEstado = !expandedEstado }
        ) {
            OutlinedTextField(
                value = estado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = campoColores  // Aplicamos los mismos colores de la paleta
            )
            ExposedDropdownMenu(
                expanded = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estados.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            estado = it
                            expandedEstado = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Etiquetas", style = MaterialTheme.typography.titleMedium)

        viewModel.etiquetas.forEach { etiqueta ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedEtiquetas.contains(etiqueta.id),
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedEtiquetas.add(etiqueta.id)
                        } else {
                            selectedEtiquetas.remove(etiqueta.id)
                        }
                    }
                )
                Text(etiqueta.name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CATEGORIA DROPDOWN
        var expandedCategoria by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = !expandedCategoria }
        ) {
            OutlinedTextField(
                value = categorias.first { it.first == categoriaId }.second,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = campoColores  // Aplicamos los mismos colores de la paleta
            )
            ExposedDropdownMenu(
                expanded = expandedCategoria,
                onDismissRequest = { expandedCategoria = false }
            ) {
                categorias.forEach {
                    DropdownMenuItem(
                        text = { Text(it.second) },
                        onClick = {
                            categoriaId = it.first
                            expandedCategoria = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // FECHA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Antigüedad") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                // "enabled = false" hace que use los colores "disabled".
                // Por eso añadimos disabledBorderColor y disabledLabelColor en campoColores
                colors = campoColores
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Reemplazamos el Button genérico por nuestro componente LoopBoton
        // La lógica de validación y guardado va dentro del onClick igual que antes
        LoopBoton(
            texto = "Guardar Producto",
            onClick = {

                if (selectedDate.isEmpty()) {
                    viewModel.errorMessage = "Selecciona una fecha"
                    return@LoopBoton
                }

                if (imageUris.isEmpty()) {
                    viewModel.errorMessage = "Selecciona al menos una imagen"
                    return@LoopBoton
                }

                viewModel.createProduct(
                    token = token,
                    context = context,
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio.toDoubleOrNull() ?: 0.0,
                    estado = estado,
                    ubicacion = ubicacion,
                    antiguedad = selectedDate,
                    categoriaId = categoriaId,
                    imageUris = imageUris
                )
            }
        )

        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        } // Cierre del Column interior (el que tiene padding)
    } // Cierre del Column exterior (el que tiene el scroll)
}