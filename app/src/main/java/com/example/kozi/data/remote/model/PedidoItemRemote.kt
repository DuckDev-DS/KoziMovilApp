package com.example.kozi.data.remote.model

data class PedidoItemRemote(
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
)