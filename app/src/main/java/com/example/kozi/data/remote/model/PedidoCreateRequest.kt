package com.example.kozi.data.remote.model

data class PedidoCreateRequest(
    val usuarioId: Long,
    val subtotal: Double,
    val descuento: Double,
    val total: Double,
    val items: List<PedidoItemRequest>
)