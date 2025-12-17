package com.example.kozi.data.remote.model

data class PedidoRemote(
    val id: Long,
    val fechaCreacion: String,
    val total: Double,
    val usuario: UsuarioRemote,
    val estado: EstadoRemote,
    val envio: EnvioRemote,
    val pago: PagoRemote
)

data class UsuarioRemote(
    val id: Long,
    val nombreUsuario: String,
    val email: String,
    val password: String?,
    val fotoPerfil: String?,
    val activo: Boolean,
    val rol: RolRemote?,
    val membresia: MembresiaRemote?
)

data class EstadoRemote(
    val id: Long,
    val tipoEstado: String
)

data class EnvioRemote(
    val id: Long,
    val metodoEnvio: String
)

data class PagoRemote(
    val id: Long,
    val tipoPago: String
)
