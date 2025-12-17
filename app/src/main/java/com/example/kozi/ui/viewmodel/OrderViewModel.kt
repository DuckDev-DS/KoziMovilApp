package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.local.CartDao
import com.example.kozi.data.local.OrderDao
import com.example.kozi.data.local.OrderEntity
import com.example.kozi.data.local.OrderItemEntity
import com.example.kozi.data.local.OrderWithItems
import com.example.kozi.data.remote.KoziApiClient
import com.example.kozi.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.example.kozi.data.remote.model.PedidoCreateRequest
import com.example.kozi.data.remote.model.UsuarioIdRef
import com.example.kozi.data.remote.model.EstadoIdRef
import com.example.kozi.data.remote.model.EnvioIdRef
import com.example.kozi.data.remote.model.PagoIdRef
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class OrderViewModel(
    private val orderDao: OrderDao,
    private val cartDao: CartDao
) : ViewModel() {

    fun observeOrdersFor(email: String): Flow<List<OrderWithItems>> {
        return orderDao.observeOrdersFor(email)
    }

    fun createOrderFromCartOnline(
        currentUser: User,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val cartItems = cartDao.getCart()
                if (cartItems.isEmpty()) {
                    onResult(false, "El carrito está vacío")
                    return@launch
                }

                val subtotal = cartItems.sumOf { it.price * it.quantity }
                val discount = if (currentUser.isVip) subtotal * 0.2 else 0.0
                val total = subtotal - discount
                val now = System.currentTimeMillis()
                val isoFormatter = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.US
                ).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }

                val fechaCreacion = isoFormatter.format(Date())

                if (currentUser.id <= 0) {
                    onResult(false, "Usuario inválido (id=${currentUser.id}). Inicia sesión nuevamente.")
                    return@launch
                }

                // IDs reales
                val request = PedidoCreateRequest(
                    fechaCreacion = fechaCreacion,
                    usuario = UsuarioIdRef(currentUser.id.toLong()),
                    estado = EstadoIdRef(1L), // Pagado
                    envio = EnvioIdRef(1L),  // Chilexpress
                    pago = PagoIdRef(1L),    // Crédito
                    total = total
                )

                val response = KoziApiClient.api.createPedido(request)

                if (!response.isSuccessful) {
                    onResult(false, "Error al crear pedido (${response.code()})")
                    return@launch
                }

                val pedidoRemote = response.body()
                if (pedidoRemote == null) {
                    onResult(false, "Respuesta inválida del servidor")
                    return@launch
                }

                // Guardar copia local (opcional)
                val orderId = orderDao.insertOrder(
                    OrderEntity(
                        userEmail = currentUser.email,
                        createdAt = now,
                        subtotal = subtotal,
                        discount = discount,
                        total = pedidoRemote.total
                    )
                )

                val items = cartItems.map {
                    OrderItemEntity(
                        orderId = orderId,
                        productId = it.productId,
                        name = it.name,
                        price = it.price,
                        quantity = it.quantity
                    )
                }

                orderDao.insertItems(items)
                cartDao.clear()

                onResult(true, null)

            } catch (e: Exception) {
                onResult(false, "Error de conexión con el servidor")
            }
        }
    }


    fun checkoutLocal(
        userEmail: String,
        isVip: Boolean,
        onDone: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val cartItems = cartDao.getCart()
            if (cartItems.isEmpty()) {
                onDone?.invoke()
                return@launch
            }

            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val discount = if (isVip) subtotal * 0.2 else 0.0
            val total = subtotal - discount
            val now = System.currentTimeMillis()

            val orderId = orderDao.insertOrder(
                OrderEntity(
                    userEmail = userEmail,
                    createdAt = now,
                    subtotal = subtotal,
                    discount = discount,
                    total = total
                )
            )

            val items = cartItems.map {
                OrderItemEntity(
                    orderId = orderId,
                    productId = it.productId,
                    name = it.name,
                    price = it.price,
                    quantity = it.quantity
                )
            }
            orderDao.insertItems(items)
            cartDao.clear()

            onDone?.invoke()
        }
    }


    fun syncPedidosFromBackend(
        currentUser: User,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = KoziApiClient.api.getPedidosByUsuario(currentUser.id.toLong())

                if (response.code() == 404) {
                    onResult(false, "Usuario no encontrado en el servidor")
                    return@launch
                }
                if (response.code() == 204) {
                    onResult(true, null)
                    return@launch
                }
                if (!response.isSuccessful) {
                    onResult(false, "Error al sincronizar pedidos (${response.code()})")
                    return@launch
                }

                val pedidos = response.body().orEmpty()

                // Guarda cabecera local
                pedidos.forEach { pedido ->
                    orderDao.insertOrder(
                        OrderEntity(
                            userEmail = currentUser.email,
                            createdAt = System.currentTimeMillis(),
                            subtotal = pedido.total,
                            discount = 0.0,
                            total = pedido.total
                        )
                    )
                }

                onResult(true, null)

            } catch (e: Exception) {
                onResult(false, "Error de conexión con el servidor")
            }
        }
    }

}
