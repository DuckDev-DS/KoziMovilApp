package com.example.kozi.data.remote.model


data class PedidoItemRequest(
    val productoId: Int,
    val cantidad: Int,
    val precioUnitario: Double
)