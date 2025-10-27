package com.example.kozi.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Cart : Screen("cart")
    object User : Screen("user")
    // Amo esta wea
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }

    // Para el futuro
    object Login : Screen("login")
    object Register : Screen("register")
}