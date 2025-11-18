package com.example.kozi.data.remote

import com.example.kozi.data.remote.model.LoginRequest
import com.example.kozi.data.remote.model.Producto
import com.example.kozi.data.remote.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface KoziApiService {

    // Healthcheck
    @GET("api/health")
    suspend fun health(): String

    // Productos
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

    // Login de usuario
    @POST("api/usuarios/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<Usuario>
}
