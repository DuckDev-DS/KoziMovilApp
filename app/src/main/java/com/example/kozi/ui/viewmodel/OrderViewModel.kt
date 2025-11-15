package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.local.CartDao
import com.example.kozi.data.local.OrderDao
import com.example.kozi.data.local.OrderEntity
import com.example.kozi.data.local.OrderItemEntity
import com.example.kozi.data.local.OrderWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderDao: OrderDao,
    private val cartDao: CartDao
) : ViewModel() {

    fun observeOrdersFor(email: String): Flow<List<OrderWithItems>> =
        orderDao.observeOrdersFor(email)

    fun checkout(userEmail: String, isVip: Boolean, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            val cartItems = cartDao.getCart()
            if (cartItems.isEmpty()) {
                onDone?.invoke()
                return@launch
            }

            val subtotal = cartItems.sumOf { it.price * it.quantity }
            val discount = if (isVip) subtotal * 0.2 else 0.0
            val total = subtotal - discount

            val orderId = orderDao.insertOrder(
                OrderEntity(
                    userEmail = userEmail,
                    createdAt = System.currentTimeMillis(),
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
}
