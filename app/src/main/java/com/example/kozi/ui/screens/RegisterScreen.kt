package com.example.kozi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kozi.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.collectAsState().value
    val currentUser = authViewModel.currentUser.collectAsState().value
    val expandedState = remember { mutableStateOf(false) }

    // Si el registro es exitoso, regresar automáticamente
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear Cuenta") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Únete a la comunidad KOZI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de nombre
            OutlinedTextField(
                value = authState.name,
                onValueChange = { authViewModel.onNameChange(it) },
                label = { Text("Nombre completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Nombre")
                },
                modifier = Modifier.fillMaxWidth(),
                isError = authState.errors.name != null,
                supportingText = {
                    authState.errors.name?.let { errorMessage ->
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo
            OutlinedTextField(
                value = authState.email,
                onValueChange = { authViewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = authState.errors.email != null,
                supportingText = {
                    authState.errors.email?.let { errorMessage ->
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = authState.password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Contraseña")
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = authState.errors.password != null,
                supportingText = {
                    authState.errors.password?.let { errorMessage ->
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de confirmar contraseña
            OutlinedTextField(
                value = authState.confirmPassword,
                onValueChange = { authViewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Confirmar contraseña")
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = authState.errors.confirmPassword != null,
                supportingText = {
                    authState.errors.confirmPassword?.let { errorMessage ->
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DROPDOWN para tipo de usuario
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (authState.isVip) "VIP (20% de descuento)" else "Normal",
                    onValueChange = { },
                    label = { Text("Tipo de usuario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedState.value = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expandedState.value = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar tipo de usuario"
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = expandedState.value,
                    onDismissRequest = { expandedState.value = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Normal") },
                        onClick = {
                            authViewModel.onVipChange(false)
                            expandedState.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("VIP (20% de descuento)") },
                        onClick = {
                            authViewModel.onVipChange(true)
                            expandedState.value = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de registro
            Button(
                onClick = {
                    authViewModel.registerUser()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState.name.isNotEmpty() &&
                        authState.email.isNotEmpty() &&
                        authState.password.isNotEmpty() &&
                        authState.confirmPassword.isNotEmpty() &&
                        authState.password == authState.confirmPassword
            ) {
                Text("Crear Cuenta")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto para redirigir a login
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}