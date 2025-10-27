package com.example.kozi.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        icon = Icons.Default.Home,
        title = "Inicio"
    )

    object Cart : BottomNavItem(
        route = Screen.Cart.route,
        icon = Icons.Default.ShoppingCart,
        title = "Carrito"
    )

    object User : BottomNavItem(
        route = Screen.User.route,
        icon = Icons.Default.Person,
        title = "Usuario"
    )
}