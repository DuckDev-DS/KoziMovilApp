package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.local.CartDao
import com.example.kozi.data.local.CartItemEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

class CartViewModel(private val dao: CartDao) : ViewModel() {
    val items: StateFlow<List<CartItemEntity>> =
        dao.observeCart().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun add(productId: Int, name: String, price: Double, imageRes: Int?) =
        viewModelScope.launch {
            dao.upsert(CartItemEntity(productId = productId, name = name, price = price, imageRes = imageRes, quantity = 1))
        }

    fun inc(item: CartItemEntity) = viewModelScope.launch {
        dao.updateQty(item.id, item.quantity + 1)
    }

    fun dec(item: CartItemEntity) = viewModelScope.launch {
        dao.updateQty(item.id, max(1, item.quantity - 1))
    }

    fun remove(item: CartItemEntity) = viewModelScope.launch {
        dao.delete(item.id)
    }

    fun clear() = viewModelScope.launch { dao.clear() }
}
