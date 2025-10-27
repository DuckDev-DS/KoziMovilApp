package com.example.kozi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kozi.ui.components.ProfileImage
import com.example.kozi.ui.viewmodel.AuthViewModel
import com.example.kozi.ui.viewmodel.MainViewModel
import com.example.kozi.ui.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val currentUser = authViewModel.currentUser.collectAsState().value
    val profileViewModel: ProfileViewModel = viewModel()
    val profileImageUri = profileViewModel.profileImageUri.collectAsState().value
    val context = LocalContext.current

    // Obtener órdenes del usuario actual
    val allOrders = mainViewModel.orders.collectAsState().value
    val userOrders = if (currentUser != null) {
        mainViewModel.getOrdersByUser(currentUser.id)
    } else {
        emptyList()
    }

    // Estado para controlar la visibilidad del diálogo
    val showDialog = remember { mutableStateOf(false) }

    // Variable para el URI temporal de la cámara
    val tempImageUriState = rememberSaveable { mutableStateOf(android.net.Uri.EMPTY) }
    val tempImageUri = tempImageUriState.value

    // Función para crear archivo temporal
    fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                profileViewModel.updateProfileImage(uri)
            }
        }
    )

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempImageUri != android.net.Uri.EMPTY) {
                profileViewModel.updateProfileImage(tempImageUri)
            }
        }
    )

    // Launcher para permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Función local para abrir cámara
            fun openCameraAfterPermission() {
                try {
                    val tempFile = createTempImageFile()
                    val uri = androidx.core.content.FileProvider.getUriForFile(
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
            openCameraAfterPermission()
        }
    }

    // Función para manejar el clic en cámara
    fun handleCameraClick() {
        val hasCameraPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission) {
            // Función local para abrir cámara
            fun openCameraDirect() {
                try {
                    val tempFile = createTempImageFile()
                    val uri = androidx.core.content.FileProvider.getUriForFile(
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
            openCameraDirect()
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // Redirigir automáticamente a login si no hay usuario
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo("user") { inclusive = true }
            }
        }
    }

    // Si no hay usuario, mostrar loading
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                ProfileImage(
                    imageUri = profileImageUri,
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto clicable para cambiar foto
            Text(
                text = if (profileImageUri != null) "Deseas cambiar la foto" else "Deseas ponerte una foto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        showDialog.value = true
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Información del usuario
            Text(
                text = currentUser.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            if (currentUser.isVip) {
                Text(
                    text = "🌟 Usuario VIP - 20% de descuento",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Historial de Compras",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (userOrders.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Sin compras",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aún no has realizado compras",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Lista de órdenes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(userOrders) { order ->
                        OrderItemCard(order = order)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de cerrar sesión
            Button(
                onClick = {
                    authViewModel.logout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }

        // Diálogo para elegir entre cámara y galería
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                },
                title = {
                    Text(text = "Seleccionar foto")
                },
                text = {
                    Text("Elige de dónde quieres obtener tu foto de perfil")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            galleryLauncher.launch("image/*")
                        }
                    ) {
                        Text("Galería")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            handleCameraClick()
                        }
                    ) {
                        Text("Cámara")
                    }
                }
            )
        }
    }
}

// mostrar cada orden en el historial
@Composable
fun OrderItemCard(order: com.example.kozi.model.Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // ID de la orden
            Text(
                text = "Orden: ${order.id.takeLast(6)}", // Mostramos solo los últimos 6 caracteres
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Información de productos
            Text(
                text = "Productos: ${order.products.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Total y descuento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${order.total.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (order.isVip) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Descuento VIP:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "-$${order.discount.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}