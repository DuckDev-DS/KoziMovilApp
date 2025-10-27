package com.example.kozi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kozi.ui.components.BottomNavigationBar
import com.example.kozi.ui.navigation.Screen
import com.example.kozi.ui.screens.*
import com.example.kozi.ui.theme.KOziTheme
import com.example.kozi.ui.viewmodel.AuthViewModel
import com.example.kozi.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KOziTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                        composable(Screen.Cart.route) {
                            CartScreen(
                                navController = navController,
                                viewModel = mainViewModel,
                                authViewModel = authViewModel
                            )
                        }
                        composable(Screen.User.route) {
                            UserScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                mainViewModel = mainViewModel
                            )
                        }
                        composable(Screen.Login.route) {
                            LoginScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                        composable(
                            route = Screen.ProductDetail.route,
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
                            ProductDetailScreen(
                                productId = productId,
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}