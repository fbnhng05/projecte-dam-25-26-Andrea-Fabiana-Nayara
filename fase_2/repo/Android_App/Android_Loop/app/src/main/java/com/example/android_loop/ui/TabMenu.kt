package com.example.android_loop.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.android_loop.R


@Composable
fun TabMenu(navController: NavHostController){

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
                    enabled = currentRoute != route, //desactiva el icono cuando este en la propia pantalla
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
