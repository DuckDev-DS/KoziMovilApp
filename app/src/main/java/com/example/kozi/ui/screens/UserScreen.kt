package com.example.kozi.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kozi.data.local.OrderWithItems
import com.example.kozi.ui.components.ProfileImage
import com.example.kozi.ui.viewmodel.AuthViewModel
import com.example.kozi.ui.viewmodel.MainViewModel
import com.example.kozi.ui.viewmodel.OrderViewModel
import com.example.kozi.ui.viewmodel.ProfileViewModel
import com.example.kozi.ui.viewmodel.SessionViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    sessionVm: SessionViewModel,
    orderVm: OrderViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val profileViewModel: ProfileViewModel = viewModel()
    val profileImageUri by profileViewModel.profileImageUri.collectAsState()
    val context = LocalContext.current

    // ========= MANEJO DE FOTO DE PERFIL =========
    val showDialog = remember { mutableStateOf(false) }
    val tempImageUriState = rememberSaveable { mutableStateOf(android.net.Uri.EMPTY) }
    val tempImageUri = tempImageUriState.value

    fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) profileViewModel.updateProfileImage(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != android.net.Uri.EMPTY) {
            profileViewModel.updateProfileImage(tempImageUri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val tempFile = createTempImageFile()
                val uri = FileProvider.getUriForFile(
                    context,
                    "com.example.kozi.fileprovider",
                    tempFile
                )
                tempImageUriState.value = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleCameraClick() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            try {
                val tempFile = createTempImageFile()
                val uri = FileProvider.getUriForFile(
                    context,
                    "com.example.kozi.fileprovider",
                    tempFile
                )
                tempImageUriState.value = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ========= ESTADO DE CARGA / SIN USUARIO =========
    if (currentUser == null) {
        // En vez de navegar desde aquí (que da problemas),
        // mostramos un mensaje y un botón para ir a login.
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sesión cerrada", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Inicia sesión para ver tu perfil",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Ir a iniciar sesión")
                }
            }
        }
        return
    }

    // A partir de aquí currentUser NO es nulo
    val user = currentUser!!

    // ========= HISTORIAL DESDE ROOM (PERSISTENCIA LOCAL) =========
    val roomOrders by orderVm
        .observeOrdersFor(user.email)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Foto de perfil
        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            ProfileImage(imageUri = profileImageUri, modifier = Modifier.size(120.dp))
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (profileImageUri != null) "¿Deseas cambiar la foto?" else "¿Deseas ponerte una foto?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { showDialog.value = true }
        )

        Spacer(Modifier.height(16.dp))

        // Info del usuario
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (user.isVip) {
            Text(
                text = "🌟 Usuario VIP - 20% de descuento",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(24.dp))

        // ======= HISTORIAL DE COMPRAS (Room) =======
        Text(text = "Historial de Compras", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (roomOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Sin compras",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Aún no has realizado compras",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "¡Agrega productos al carrito y realiza tu primera compra!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roomOrders) { orderWithItems ->
                    OrderItemCard(orderWithItems)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // BOTÓN CERRAR SESIÓN
        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }

    // ========= DIÁLOGO PARA ELEGIR CÁMARA / GALERÍA =========
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Seleccionar foto") },
            text = { Text("Elige de dónde quieres obtener tu foto de perfil") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        galleryLauncher.launch("image/*")
                    }
                ) { Text("Galería") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        handleCameraClick()
                    }
                ) { Text("Cámara") }
            }
        )
    }
}

@Composable
fun OrderItemCard(order: OrderWithItems) {
    val productCount = order.items.sumOf { it.quantity }
    val isVip = order.order.discount > 0.0
    val subtotal = order.order.subtotal
    val discount = order.order.discount
    val total = order.order.total
    val idText = order.order.id.toString().takeLast(6)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orden #$idText",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isVip) {
                    Text(
                        text = "🌟 VIP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$productCount producto${if (productCount != 1) "s" else ""} comprado${if (productCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Column(Modifier.fillMaxWidth()) {
                if (isVip) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Subtotal:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "$${subtotal.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Descuento VIP:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "-$${discount.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "$${total.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
