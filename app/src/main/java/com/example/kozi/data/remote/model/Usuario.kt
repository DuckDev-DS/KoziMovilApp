package com.example.kozi.data.remote.model

data class Usuario(
    val id: Long,
<<<<<<< HEAD
    val nombreUsuario: String,
    val email: String,
    val password: String?,     // writeOnly en backend, aquí puede venir null
    val fotoPerfil: String?,
    val activo: Boolean,
    val rol: RolRemote?,
    val membresia: MembresiaRemote?
)
=======
    val nombre: String,
    val correo: String,
    val tipoMembresia: String?,
    val activo: Boolean,
    val rol: Rol?
)
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
