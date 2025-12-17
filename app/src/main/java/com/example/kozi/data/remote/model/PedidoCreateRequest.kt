package com.example.kozi.data.remote.model

data class PedidoCreateRequest(
    val fechaCreacion: String,
    val usuario: UsuarioIdRef,
    val estado: EstadoIdRef,
    val envio: EnvioIdRef,
    val pago: PagoIdRef,
    val total: Double
)

data class UsuarioIdRef(val id: Long)
data class EstadoIdRef(val id: Long)
data class EnvioIdRef(val id: Long)
data class PagoIdRef(val id: Long)
