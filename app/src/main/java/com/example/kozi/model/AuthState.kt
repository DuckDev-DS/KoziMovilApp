package com.example.kozi.model

// Estado del formulario de autenticación (login y registro)
data class AuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isVip: Boolean = false,
    val errors: AuthErrors = AuthErrors()
)

// Errores de validación para cada campo
data class AuthErrors(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null
)