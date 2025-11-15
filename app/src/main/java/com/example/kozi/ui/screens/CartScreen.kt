package com.example.kozi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kozi.ui.viewmodel.AuthViewModel
import com.example.kozi.ui.viewmodel.CartViewModel
import com.example.kozi.ui.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController? = null,
    cartVm: CartViewModel,
    authViewModel: AuthViewModel,
    orderVm: OrderViewModel
) {
    val items = cartVm.items.collectAsState().value
    val subtotal = items.sumOf { it.price * it.quantity }
    val user = authViewModel.currentUser.collectAsState().value
    val isVip = user?.isVip == true
    val discount = if (isVip) subtotal * 0.2 else 0.0
    val total = subtotal - discount

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Carrito") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(Modifier.weight(1f)) {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(4.dp))
                                    Text("$${item.price}")
                                }
                                IconButton(onClick = { cartVm.dec(item) }) {
                                    Icon(Icons.Default.RemoveCircle, contentDescription = "Disminuir")
                                }
                                Text("x${item.quantity}")
                                IconButton(onClick = { cartVm.inc(item) }) {
                                    Icon(Icons.Default.AddCircle, contentDescription = "Aumentar")
                                }
                                IconButton(onClick = { cartVm.remove(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Quitar")
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:"); Text("$${subtotal.toInt()}")
                        }
                        if (isVip) {
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Descuento VIP:", color = MaterialTheme.colorScheme.primary)
                                Text("-$${discount.toInt()}", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Divider(Modifier.padding(vertical = 8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("$${total.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { navController?.navigate("home") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) { Text("Seguir comprando") }

                            Button(
                                onClick = {
                                    val u = user
                                    if (u != null) {
                                        orderVm.checkout(
                                            userEmail = u.email,
                                            isVip = u.isVip,
                                            onDone = { /* sin navegación, UserScreen mostrará historial */ }
                                        )
                                    } else {
                                        navController?.navigate("login")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Pagar") }
                        }
                    }
                }
            }
        }
    }
}
