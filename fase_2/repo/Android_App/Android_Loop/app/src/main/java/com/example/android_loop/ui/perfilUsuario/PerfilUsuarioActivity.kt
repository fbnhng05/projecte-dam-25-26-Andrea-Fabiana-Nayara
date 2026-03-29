package com.example.android_loop.ui.perfilUsuario

import android.content.Context.MODE_PRIVATE
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.decode
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.net.Uri // necesario para las reseñas
import com.example.android_loop.R
// ── INICIO reseñas ──
import com.example.android_loop.ui.comentarios.ComentariosViewModel
import com.example.android_loop.ui.comentarios.ComentarioBurbuja
// ── FIN reseñas ──
import com.example.android_loop.ui.theme.Android_LoopTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField

@Composable
fun PerfilUsuario(navController: NavHostController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("loop_prefs", MODE_PRIVATE)
    val storedToken = prefs.getString("token", null)

    val viewModelGetUserData: PerfilUsuarioViewModel = viewModel()
    val perfilState = viewModelGetUserData.getUserDataState

    // ── INICIO reseñas ──
    val comentariosViewModel: ComentariosViewModel = viewModel()
    // ── FIN reseñas ──

    var username by remember { mutableStateOf("María") }
    // ── INICIO reseñas ──
    var userId by remember { mutableIntStateOf(0) }
    // ── FIN reseñas ──
    var image_1920 by remember { mutableStateOf("") }
    val defaultAvatar = ImageBitmap.imageResource(R.drawable.no_avatar)
    var avatarImage by remember { mutableStateOf<ImageBitmap?>(defaultAvatar) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("En venta", "Reseñas")

    var filtro by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        storedToken?.let {
            viewModelGetUserData.getUserData(it)
        }
    }

    LaunchedEffect(perfilState) {
        perfilState?.onSuccess { user ->
            username = user.username
            image_1920 = user.image_1920
            userId = user.id
            // ── INICIO reseñas ──
            storedToken?.let { comentariosViewModel.cargarUsuarioActual(it) }
            comentariosViewModel.cargarComentarios(user.id)
            // ── FIN reseñas ──
        }

        if (!image_1920.isNullOrBlank() && image_1920 != "false") {
            //conversion base64 a Image
            val decodedString = decode(image_1920, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            avatarImage = bitmap.asImageBitmap()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Botón ajustes (esquina superior derecha)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { navController.navigate("ajustes") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = Color(0xFF003459),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,) {
                    avatarImage?.let { img ->
                        Image(
                            bitmap = img,
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                        )
                    }

                    // Botón lápiz
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Cambiar imagen",
                            tint = Color(0xFF003459),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.Companion.height(8.dp))

                    Text(username)

                }

            }

            Spacer(Modifier.Companion.height(30.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        indicator = { tabPositions ->
                            Box(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(4.dp)
                                    .background(Color(0xFF003459))
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(
                                    title,
                                    color = Color(0xFF003459),
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(textDecoration = TextDecoration.None),
                                    maxLines = 1,
                                    overflow = TextOverflow.Visible,
                                    )
                                },
                                modifier = Modifier
                                    .background(
                                        Color.White
                                    )
                                    .padding(vertical = 8.dp, horizontal = 20.dp)
                                    .clip(RoundedCornerShape(30.dp))
                                    .let {
                                        if (selectedTab == index) {
                                            it.clip(RoundedCornerShape(30.dp))
                                        } else it
                                    }
                            )
                        }
                    }

                    when (selectedTab) {
                        0 -> {
                            TextField(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp)),
                                value = filtro,
                                onValueChange = {
                                    filtro = it
                                },
                                placeholder = { Text("Buscar producto") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.lupa),
                                        contentDescription = "Buscar",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.LightGray,
                                    unfocusedContainerColor = Color.LightGray,
                                    disabledContainerColor = Color.LightGray,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )

                            )
                            //TODO: 1. Crear lista de productos del usuario
                            //TODO: 2. Si hay filtros, filtrar la lista
                            //TODO: 3. Mostrar los productos en LazyColumn
                        }
                        // ── INICIO reseñas ──
                        1 -> {
                            val comentarios = comentariosViewModel.comentarios
                            val isLoading = comentariosViewModel.isLoading
                            val currentUser = comentariosViewModel.currentUserName
                            var textoResena by remember { mutableStateOf("") }
                            val enviado = comentariosViewModel.comentarioEnviado

                            LaunchedEffect(enviado) {
                                if (enviado) {
                                    textoResena = ""
                                    comentariosViewModel.resetComentarioEnviado()
                                }
                            }

                            if (isLoading && comentarios.isEmpty()) {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF003459))
                                }
                            } else if (comentarios.isEmpty()) {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Aún no hay reseñas", color = Color.Gray)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    comentarios.forEach { comentario ->
                                        ComentarioBurbuja(
                                            comentario = comentario,
                                            esMio = comentario.comentador == currentUser,
                                            onPerfilClick = { id, nombre ->
                                                navController.navigate("perfilVendedor/$id/${Uri.encode(nombre)}")
                                            }
                                        )
                                    }
                                }
                            }

                        }
                        // ── FIN reseñas ──
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilUsuarioPreview() {
    Android_LoopTheme {
        PerfilUsuario(navController = rememberNavController())
    }
}