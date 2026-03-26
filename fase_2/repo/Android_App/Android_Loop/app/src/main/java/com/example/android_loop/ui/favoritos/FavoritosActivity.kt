package com.example.android_loop.ui.favoritos

import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.android_loop.R
import com.example.android_loop.ui.theme.Android_LoopTheme
import java.text.Normalizer

@Composable
fun Favoritos(navController: NavHostController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("loop_prefs", MODE_PRIVATE)
    val storedToken = prefs.getString("token", null)

    var filtro by rememberSaveable { mutableStateOf("") }

    val viewModelFavoritos : FavoritosViewModel = viewModel()
    val favState = viewModelFavoritos.favState

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        // Llamar al endpoint

        LaunchedEffect(Unit)  {
            viewModelFavoritos.favoritosGet(storedToken!!)
        }

        // UI

        Column(
            Modifier.fillMaxSize().padding(top = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TODO: Código interfaz

            // Barra de búsqueda con filtros

            TextField(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp)),
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

            // TODO: Lista de productos en favoritos

            when (favState) {

                is FavoritosUiState.SuccessGet -> {
                    var lista = favState.result

                    // Agregar filtros si los hay

                    if (!filtro.isEmpty()) lista = lista.filter {
                        it.nombre.sinAcentos().lowercase().contains(filtro.sinAcentos().lowercase())
                    }

                    if (lista.isEmpty()) {
                        Text("No hay productos en favoritos")
                    } else {
                        LazyColumn {
                            items(lista) { producto ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                                    ) {
                                        Column {
                                            Text(text = producto.nombre)

                                            Text(text = producto.descripcion)

                                            Text(text = "Precio: ${producto.precio}")

                                            Text(text = producto.ubicacion)
                                        }
                                        Icon(
                                            painter = painterResource(id = R.drawable.trash),
                                            contentDescription = "Eliminar",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .clickable {
                                                    Log.d("Icono", "${producto.nombre} se ha eliminado de favoritos")
                                                    viewModelFavoritos.favoritosDelete(storedToken!!, producto.id)
                                                },
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is FavoritosUiState.ErrorGet -> Text("Ha ocurrido un error")

                else -> {}
            }

        }

        // UI de carga

        if (favState is FavoritosUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.Blue
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun FavoritosPreview() {
    Android_LoopTheme {
        Favoritos(navController = rememberNavController())
    }
}

/**
 * Quitar acentos en los filtros
 */
fun String.sinAcentos(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}