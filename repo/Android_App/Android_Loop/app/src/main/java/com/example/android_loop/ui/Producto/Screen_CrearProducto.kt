package com.example.android_loop.data.Producto

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: _02_ProductViewModel,
    navController: NavController
) {


    val context = LocalContext.current
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
        imageUris.clear()
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
        viewModel.loadEtiquetas()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Crear Producto", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text("Fotos", style = MaterialTheme.typography.titleMedium)

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
                        modifier = Modifier.fillMaxSize()
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

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.menuAnchor().fillMaxWidth()
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
                modifier = Modifier.menuAnchor().fillMaxWidth()
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
                enabled = false
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                if (selectedDate.isEmpty()) {
                    viewModel.errorMessage = "Selecciona una fecha"
                    return@Button
                }

                if (imageUris.isEmpty()) {
                    viewModel.errorMessage = "Selecciona al menos una imagen"
                    return@Button
                }

                viewModel.createProduct(
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }

        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}