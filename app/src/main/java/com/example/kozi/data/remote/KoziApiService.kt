package com.example.kozi.data.remote

<<<<<<< HEAD
import com.example.kozi.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface KoziApiService {

    // =======================
    //        HEALTH
    // =======================
    @GET("api/health")
    suspend fun health(): String

    // =======================
    //        PRODUCTOS
    // =======================
=======
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
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
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

<<<<<<< HEAD

    // =======================
    //        USUARIOS
    // =======================

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
=======
    // Login de usuario
    @POST("api/usuarios/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<Usuario>
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
}
