package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.local.CartDao
import com.example.kozi.data.local.OrderDao
import com.example.kozi.data.local.OrderEntity
import com.example.kozi.data.local.OrderItemEntity
import com.example.kozi.data.local.OrderWithItems
import com.example.kozi.data.remote.KoziApiClient
import com.example.kozi.data.remote.model.PedidoRemote
import com.example.kozi.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderDao: OrderDao,
    private val cartDao: CartDao
) : ViewModel() {

    /**
     * Historial de pedidos local (Room) para un usuario por correo.
     * Esto alimenta el UserScreen.
     */
    fun observeOrdersFor(email: String): Flow<List<OrderWithItems>> {
        return orderDao.observeOrdersFor(email)
    }

    /**
     * Checkout con PERSISTENCIA LOCAL:
     * - Lee el carrito desde Room
     * - Calcula subtotal, descuento y total
     * - Guarda el pedido en Room (OrderEntity + OrderItemEntity)
     * - Limpia el carrito
     *
     * Ya NO hace POST al backend.
     */
    fun createOrderFromCartOnline( // mantenemos el nombre para no romper llamadas
        currentUser: User,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val cartItems = cartDao.getCart()
            if (cartItems.isEmpty()) {
                onResult(false, "El carrito está vacío")
                return@launch
            }

            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val discount = if (currentUser.isVip) subtotal * 0.2 else 0.0
            val total = subtotal - discount
            val now = System.currentTimeMillis()

            // Guardar pedido en Room
            val orderId = orderDao.insertOrder(
                OrderEntity(
                    userEmail = currentUser.email,
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

            onResult(true, null)
        }
    }

    /**
     * Variante de checkout local si prefieres trabajar con email e isVip directo.
     * Hace exactamente lo mismo que el método de arriba, pero con parámetros sueltos.
     */
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

    /**
     * OPCIONAL:
     * Sincroniza el historial de compras desde el backend:
     * GET api/usuarios/{usuarioId}/pedidos
     *
     * Si quieres full local, puedes comentar todo esto o no llamarlo desde la UI.
     */
    fun syncPedidosFromBackend(
        currentUser: User,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = KoziApiClient.api.getPedidosByUsuario(currentUser.id.toLong())

                when (response.code()) {
                    404 -> {
                        onResult(false, "Usuario no encontrado en el servidor")
                        return@launch
                    }
                    204 -> {
                        // Sin pedidos remotos, no tocamos Room
                        onResult(true, null)
                        return@launch
                    }
                }

                if (!response.isSuccessful) {
                    onResult(false, "Error al sincronizar pedidos (${response.code()})")
                    return@launch
                }

                val pedidos: List<PedidoRemote> = response.body() ?: emptyList()

                pedidos.forEach { pedido ->
                    val orderId = orderDao.insertOrder(
                        OrderEntity(
                            userEmail = currentUser.email,
                            createdAt = System.currentTimeMillis(), // opcional: parsear fecha del backend
                            subtotal = pedido.subtotal,
                            discount = pedido.descuento,
                            total = pedido.total
                        )
                    )

                    val items = pedido.items.map { item ->
                        OrderItemEntity(
                            orderId = orderId,
                            productId = item.productoId,
                            name = item.nombreProducto,
                            price = item.precioUnitario,
                            quantity = item.cantidad
                        )
                    }

                    orderDao.insertItems(items)
                }

                onResult(true, null)

            } catch (e: Exception) {
                onResult(false, "Error de conexión con el servidor")
            }
        }
    }
}
