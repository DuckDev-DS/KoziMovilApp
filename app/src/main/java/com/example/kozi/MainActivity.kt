package com.example.kozi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.kozi.data.local.AppDatabase
import com.example.kozi.data.prefs.SessionStore
import com.example.kozi.ui.components.BottomNavigationBar
import com.example.kozi.ui.navigation.Screen
import com.example.kozi.ui.screens.*
import com.example.kozi.ui.theme.KOziTheme
import com.example.kozi.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KOziTheme {
                val navController = rememberNavController()

                val mainViewModel: MainViewModel = viewModel()

                val sessionStore = SessionStore(this)
                val authVmFactory = viewModelFactory { initializer { AuthViewModel(sessionStore) } }
                val authViewModel: AuthViewModel = viewModel(factory = authVmFactory)

                val sessionVmFactory = viewModelFactory { initializer { SessionViewModel(sessionStore) } }
                val sessionVm: SessionViewModel = viewModel(factory = sessionVmFactory)

                val db = remember {
                    Room.databaseBuilder(applicationContext, AppDatabase::class.java, "kozi.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
                val cartVmFactory = viewModelFactory { initializer { CartViewModel(db.cartDao()) } }
                val cartVm: CartViewModel = viewModel(factory = cartVmFactory)

                val orderVmFactory = viewModelFactory { initializer { OrderViewModel(db.orderDao(), db.cartDao()) } }
                val orderVm: OrderViewModel = viewModel(factory = orderVmFactory)

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
                                viewModel = mainViewModel,
                                cartVm = cartVm
                            )
                        }
                        composable(Screen.Cart.route) {
                            CartScreen(
                                navController = navController,
                                cartVm = cartVm,
                                authViewModel = authViewModel,
                                orderVm = orderVm // si quieres hacer checkout desde aquí
                            )
                        }
                        composable(Screen.User.route) {
                            UserScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                mainViewModel = mainViewModel,
                                sessionVm = sessionVm,
                                orderVm = orderVm // historial en UserScreen
                            )
                        }
                        composable(Screen.Login.route) {
                            LoginScreen(
                                navController = navController,
                                authViewModel = authViewModel,
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
                                viewModel = mainViewModel,
                                cartVm = cartVm
                            )
                        }
                    }
                }
            }
        }
    }
}
