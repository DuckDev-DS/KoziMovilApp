package com.example.kozi.data.remote.model

data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val stock: Int
)