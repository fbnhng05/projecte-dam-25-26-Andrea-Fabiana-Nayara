package com.example.android_loop.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.android_loop.R
import com.example.android_loop.ui.perfilUsuario.TabMenu
import com.example.android_loop.ui.theme.Android_LoopTheme

@Composable
fun Home(navController: NavHostController) {
    Text("Esta es la pantalla de home")


    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        // TODO: Código interfaz



        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            TabMenu(navController)
        }
    }
}

@Composable
fun TabMenu(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        "home",
        "favoritos",
        "pantalla_listado",
        "crear_producto",
        "perfilUsuario",
    )

    NavigationBar(
        containerColor = Color(0xFF003459),
        tonalElevation = 8.dp,
        modifier = Modifier
            .height(65.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
    ) {
        items.forEachIndexed { index, route ->

            val icon = when(route) {
                "home" -> R.drawable.home
                "favoritos" -> R.drawable.empty_heart
                "pantalla_listado" -> R.drawable.shop
                "crear_producto" -> R.drawable.add
                "perfilUsuario" -> R.drawable.user
                else -> R.drawable.home
            }

            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(0.8f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Android_LoopTheme {
        Home(navController = rememberNavController())
    }
}