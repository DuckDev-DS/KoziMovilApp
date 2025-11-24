package com.example.kozi.data.remote.model

data class PedidoRemote(
    val id: Long,
    val usuarioId: Long,
    val subtotal: Double,
    val descuento: Double,
    val total: Double,
    val fechaCreacion: String? = null,
    val items: List<PedidoItemRemote> = emptyList()
)