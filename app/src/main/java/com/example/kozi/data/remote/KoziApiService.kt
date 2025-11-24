package com.example.kozi.data.remote

import com.example.kozi.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface KoziApiService {


    @GET("api/health")
    suspend fun health(): String


    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    @GET("api/productos/{id}")
    suspend fun getProductoPorId(
        @Path("id") id: Long
    ): Producto

    @GET("api/productos/categoria/{categoriaId}")
    suspend fun getProductosPorCategoria(
        @Path("categoriaId") categoriaId: Long
    ): List<Producto>



    // REGISTRO
    @POST("api/usuarios")
    suspend fun registerUsuario(
        @Body request: RegisterRequest
    ): Response<Usuario>

    // LOGIN
    @POST("api/usuarios/login")
    suspend fun loginUsuario(
        @Body request: LoginRequest
    ): Response<Usuario>

    // Obtener usuario por ID
    @GET("api/usuarios/{id}")
    suspend fun getUsuarioById(
        @Path("id") id: Long
    ): Response<Usuario>

    // Actualizar usuario
    @PUT("api/usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Long,
        @Body request: RegisterRequest
    ): Response<Usuario>

    // Eliminar usuario
    @DELETE("api/usuarios/{id}")
    suspend fun deleteUsuario(
        @Path("id") id: Long
    ): Response<Unit>

    // Pedidos según usuario
    @GET("api/usuarios/{usuarioId}/pedidos")
    suspend fun getPedidosByUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<PedidoRemote>>
}
