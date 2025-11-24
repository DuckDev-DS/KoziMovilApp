package com.example.kozi.data.remote.model

data class Usuario(
    val id: Long,
    val nombreUsuario: String,
    val email: String,
    val password: String?,
    val fotoPerfil: String?,
    val activo: Boolean,
    val rol: RolRemote?,
    val membresia: MembresiaRemote?
)
