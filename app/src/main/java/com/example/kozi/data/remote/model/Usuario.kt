package com.example.kozi.data.remote.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val tipoMembresia: String?,
    val activo: Boolean,
    val rol: Rol?
)