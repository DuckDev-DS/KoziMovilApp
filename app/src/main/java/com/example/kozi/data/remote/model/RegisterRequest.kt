package com.example.kozi.data.remote.model

data class RegisterRequest(
    val nombreUsuario: String,
    val email: String,
    val password: String,
    val rol: RolRef,
    val membresia: MembresiaRef
)
