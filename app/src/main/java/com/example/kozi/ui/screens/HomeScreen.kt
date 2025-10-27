package com.example.kozi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kozi.R
import com.example.kozi.ui.components.ProductItem
import com.example.kozi.ui.navigation.Screen
import com.example.kozi.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val productsState = viewModel.products.collectAsState().value
    val selectedCategoryState = viewModel.selectedCategory.collectAsState().value
    val showMessageState = viewModel.showMessage.collectAsState().value

    val showCategoryDropdownState = remember { mutableStateOf(false) }

    // Snackbar para mensajes
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredProducts = if (selectedCategoryState == null) {
        productsState
    } else {
        productsState.filter { it.category.name == selectedCategoryState }
    }

    // Mostrar mensaje
    LaunchedEffect(showMessageState) {
        if (showMessageState != null) {
            snackbarHostState.showSnackbar(showMessageState)
            // Limpiar mensaje
            delay(1000)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo
                        Icon(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo kOzi",
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "kOzi",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        // Snackbar para mensajes
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtro de categorías
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { showCategoryDropdownState.value = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedCategoryState ?: "Todas las categorías",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Desplegar categorías"
                        )
                    }
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = showCategoryDropdownState.value,
                    onDismissRequest = { showCategoryDropdownState.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas las categorías") },
                        onClick = {
                            viewModel.filterByCategory(null)
                            showCategoryDropdownState.value = false
                        }
                    )
                    viewModel.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                viewModel.filterByCategory(category)
                                showCategoryDropdownState.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de productos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductItem(
                        product = product,
                        onAddToCart = {
                            viewModel.addToCart(product)
                        },
                        onProductClick = {
                            navController.navigate(Screen.ProductDetail.createRoute(product.id))
                        }
                    )
                }
            }
        }
    }
}