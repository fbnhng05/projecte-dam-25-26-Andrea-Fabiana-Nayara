package com.example.android_loop.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tuapp.ui.theme.Primary

@Composable
fun TabMenu(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val fabScale by animateFloatAsState(
        targetValue = if (currentRoute == "crear_producto") 1.1f else 1f,
        label = ""
    )

    val fabRotation by animateFloatAsState(
        targetValue = if (currentRoute == "crear_producto") 45f else 0f,
        label = ""
    )

    Box(
        modifier =  Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {

        // 🔷 NAV BAR FLOTANTE
        NavigationBar(
            containerColor = Primary,
            tonalElevation = 0.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .clip(RoundedCornerShape(28.dp))
                .shadow(12.dp, RoundedCornerShape(28.dp))
                .height(72.dp)
                .fillMaxWidth()
        ) {

            NavItem(
                selected = currentRoute == "pantalla_listado",
                icon = Icons.Default.Home
            ) { navController.navigate("pantalla_listado"){
                {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            } }

            NavItem(
                selected = currentRoute == "favoritos",
                icon = Icons.Default.FavoriteBorder
            ) { navController.navigate("favoritos"){
                {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            } }

            Spacer(modifier = Modifier.weight(1f))

            NavItem(
                selected = currentRoute == "pantalla_listado",
                icon = Icons.Default.Email
            ) { navController.navigate("pantalla_listado"){
                {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            } }

            NavItem(
                selected = currentRoute == "perfilUsuario",
                icon = Icons.Default.Person
            ) { navController.navigate("perfilUsuario"){
                {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            } }
        }

        // 🔥 FAB CENTRAL PRO
        Box(
            modifier = Modifier
                .offset(y = (-38).dp)
                .size(72.dp)
                .graphicsLayer {
                    scaleX = fabScale
                    scaleY = fabScale
                    rotationZ = fabRotation
                }
                .shadow(30.dp, CircleShape, clip = false)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color(0xFF38BDF8).copy(alpha = 0.7f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.25f),
                    CircleShape
                )
                .clickable {
                    navController.navigate("crear_producto")
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun RowScope.NavItem(
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF38BDF8),
            unselectedIconColor = Color.White.copy(0.5f),
            indicatorColor = Color.Transparent
        )
    )
}