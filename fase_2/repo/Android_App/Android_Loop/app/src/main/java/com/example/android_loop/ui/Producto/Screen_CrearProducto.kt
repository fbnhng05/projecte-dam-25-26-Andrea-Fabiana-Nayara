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
import com.example.android_loop.ui.componentes.PantallaHeader  // Nuestro header reutilizable
import com.example.android_loop.ui.componentes.LoopBoton       // Nuestro botón reutilizable
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi  // Necesario para usar FlowRow
import androidx.compose.foundation.layout.FlowRow                 // Distribuye elementos en filas automáticamente
import androidx.compose.ui.focus.onFocusChanged                   // Detecta cuando un campo recibe o pierde el foco
import com.tuapp.ui.theme.OnPrimary


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var busquedaEtiqueta by remember { mutableStateOf("") }       // Texto que escribe el usuario para filtrar
    var mostrarSugerencias by remember { mutableStateOf(false) }  // Controla si el desplegable está visible

    var estado by rememberSaveable { mutableStateOf("nuevo") }
    val estados = listOf("nuevo", "segunda_mano", "reacondicionado")

    // null = ninguna categoría seleccionada todavía (antes era 1 hardcodeado)
    var categoriaId by rememberSaveable { mutableStateOf<Int?>(null) }

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
        viewModel.loadCategorias(token)
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

    // Column exterior: ocupa toda la pantalla pero NO tiene scroll
    // El scroll solo está en el contenido, no en el botón
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Header a tope con los bordes, fuera del scroll
        PantallaHeader(titulo = "Crear Producto")

        // Column interior: weight(1f) = ocupa todo el espacio disponible excepto el botón de abajo
        // El scroll está aquí para que solo el contenido se desplace, no el botón
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
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


            // ── ETIQUETAS ──────────────────────────────────────────────────
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Etiquetas (máx. 5)",
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // PARTE 1: chips de las etiquetas ya seleccionadas
            // FlowRow las distribuye en filas automáticamente si no caben en una sola
            if (selectedEtiquetas.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    selectedEtiquetas.forEach { id ->
                        val etiqueta = viewModel.etiquetas.find { it.id == id }
                        etiqueta?.let {
                            InputChip(
                                selected = true,
                                onClick = { selectedEtiquetas.remove(id) }, // click la elimina
                                label = { Text("#${it.name}") },
                                trailingIcon = {
                                    // Icono X para quitar la etiqueta
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Quitar",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = Secondary, // Fondo azul océano
                                    selectedLabelColor = OnPrimary,     // Texto blanco
                                    selectedTrailingIconColor = OnPrimary
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // PARTE 2: campo de búsqueda (solo visible si no llegó al máximo de 5)
            if (selectedEtiquetas.size < 5) {
                OutlinedTextField(
                    value = busquedaEtiqueta,
                    onValueChange = {
                        busquedaEtiqueta = it
                        mostrarSugerencias = true // al escribir abre el desplegable
                    },
                    label = { Text("Buscar etiqueta...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            // Al hacer click en el campo también abre el desplegable
                            if (focusState.isFocused) mostrarSugerencias = true
                        },
                    colors = campoColores,
                    singleLine = true // evita que el campo crezca en altura
                )
            }

            // PARTE 3: lista desplegable filtrada según lo que escribe el usuario
            val sugerencias = viewModel.etiquetas.filter { etiqueta ->
                etiqueta.name.contains(busquedaEtiqueta, ignoreCase = true) &&
                !selectedEtiquetas.contains(etiqueta.id)
            }

            // Calculamos si el texto escrito no existe en la lista completa de etiquetas
            val textoNuevo = busquedaEtiqueta.trim()
            val noExiste = textoNuevo.isNotEmpty() &&
                viewModel.etiquetas.none { it.name.equals(textoNuevo, ignoreCase = true) }

            // El Card se muestra si hay sugerencias O si hay un texto nuevo para crear
            // Antes solo se mostraba con sugerencias, por eso desaparecía al escribir más letras
            if (mostrarSugerencias && (sugerencias.isNotEmpty() || noExiste)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    sugerencias.forEach { etiqueta ->
                        Text(
                            text = "#${etiqueta.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedEtiquetas.add(etiqueta.id)
                                    busquedaEtiqueta = ""
                                    mostrarSugerencias = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            color = Primary
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }

                    if (noExiste) {
                        Text(
                            text = "+ Crear \"#$textoNuevo\"",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.createEtiqueta(token, textoNuevo) { nuevoId ->
                                        selectedEtiquetas.add(nuevoId)
                                    }
                                    busquedaEtiqueta = ""
                                    mostrarSugerencias = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            color = Secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // CATEGORIA DROPDOWN — cargado desde Odoo, no hardcodeado
            var expandedCategoria by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    // Busca el nombre de la categoría seleccionada, o muestra el placeholder
                    value = viewModel.categorias.find { it.id == categoriaId }?.nombre ?: "Selecciona categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = campoColores
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    // Recorre las categorías del ViewModel (venidas de Odoo)
                    viewModel.categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                categoriaId = categoria.id  // Guarda el ID real de Odoo
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

        Spacer(modifier = Modifier.height(16.dp))

        } // Cierre del Column interior (el que tiene el scroll y el padding)

        // Mensaje de error — fuera del scroll, encima del botón fijo
        viewModel.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Botón fijo en la parte inferior — FUERA del scroll
        // Al estar fuera del Column con weight(1f), siempre queda visible sin desplazarse
        LoopBoton(
            texto = "Guardar Producto",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
                    categoriaId = categoriaId ?: 0, // Si no seleccionó ninguna, envía 0
                    imageUris = imageUris
                )
            }
        )

    } // Cierre del Column exterior
}