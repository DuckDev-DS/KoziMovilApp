package com.example.kozi.data.remote.model

data class Usuario(
    val id: Long,
    val nombreUsuario: String,
    val email: String,
    val password: String?,     // writeOnly en backend, aquí puede venir null
    val fotoPerfil: String?,
    val activo: Boolean,
    val rol: RolRemote?,
    val membresia: MembresiaRemote?
)
