package com.example.android_loop.ui.comentarios

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    productoId: Int,
    navController: NavController,
    viewModel: ComentariosViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = context.getSharedPreferences("loop_prefs", MODE_PRIVATE)
        .getString("token", "") ?: ""

    val comentarios = viewModel.comentarios
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val enviado = viewModel.comentarioEnviado
    val currentUser = viewModel.currentUserName

    var textoComentario by remember { mutableStateOf("") }

    LaunchedEffect(productoId) {
        viewModel.cargarUsuarioActual(token)
        viewModel.cargarComentarios(productoId)
    }

    LaunchedEffect(enviado) {
        if (enviado) {
            textoComentario = ""
            viewModel.resetComentarioEnviado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003459),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && comentarios.isEmpty() -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF003459))
                    }
                }
                error != null && comentarios.isEmpty() -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error al cargar las reseñas", color = MaterialTheme.colorScheme.error)
                    }
                }
                comentarios.isEmpty() -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aún no hay reseñas. ¡Sé el primero!", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comentarios.filter { it.estado == "published" }) { comentario ->
                            ComentarioBurbuja(
                                comentario = comentario,
                                esMio = comentario.comentador == currentUser
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textoComentario,
                    onValueChange = { textoComentario = it },
                    placeholder = { Text("Escribe tu reseña...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp)
                )
                IconButton(
                    onClick = { viewModel.enviarComentario(productoId, textoComentario) },
                    enabled = textoComentario.isNotBlank() && !isLoading
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = if (textoComentario.isNotBlank()) Color(0xFF003459) else Color.Gray
                    )
                }
            }
        }
    }
}

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
