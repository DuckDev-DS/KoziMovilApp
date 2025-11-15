package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.model.Order
import com.example.kozi.model.Product
import com.example.kozi.model.User
import com.example.kozi.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cartItems: StateFlow<Map<Product, Int>> = _cartItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _showMessage = MutableStateFlow<String?>(null)
    val showMessage: StateFlow<String?> = _showMessage.asStateFlow()

    // Historial de órdenes
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        viewModelScope.launch {
            _products.value = ProductRepository.getProducts()
        }
    }

    fun getTotalPriceWithDiscount(isVip: Boolean): Double {
        val total = getTotalPrice()
        return if (isVip) total * 0.8 else total // 20% de descuento para VIP
    }

    // Obtener monto del descuento
    fun getDiscountAmount(isVip: Boolean): Double {
        return if (isVip) getTotalPrice() * 0.2 else 0.0
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun addToCart(product: Product) {
        val currentQuantity = _cartItems.value[product] ?: 0
        _cartItems.value = _cartItems.value + (product to (currentQuantity + 1))
        _showMessage.value = "✅ ${product.name} añadido al carrito"
    }

    fun removeFromCart(product: Product) {
        val currentQuantity = _cartItems.value[product] ?: 0
        if (currentQuantity > 1) {
            _cartItems.value = _cartItems.value + (product to (currentQuantity - 1))
        } else {
            _cartItems.value = _cartItems.value - product
        }
    }

    fun removeItemFromCart(product: Product) {
        _cartItems.value = _cartItems.value - product
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.entries.sumOf { (product, quantity) ->
            product.price * quantity
        }
    }

    // guarda en historial antes de limpiar
    fun clearCart(currentUser: User? = null) {
        // Guardar en historial si hay productos y usuario
        val cartItemsCurrent = _cartItems.value
        if (cartItemsCurrent.isNotEmpty() && currentUser != null) {
            val newOrder = Order(
                id = "ORDER_${System.currentTimeMillis()}",
                userId = currentUser.id,
                products = cartItemsCurrent.toMap(), // Copiamos los productos
                total = getTotalPriceWithDiscount(currentUser.isVip),
                discount = getDiscountAmount(currentUser.isVip),
                isVip = currentUser.isVip
            )
            _orders.value = _orders.value + newOrder
        }

        // Limpiar carrito
        _cartItems.value = emptyMap()
        _showMessage.value = "🎉 ¡Compra realizada con éxito!"
    }

    fun clearMessage() {
        _showMessage.value = null
    }

    val categories: List<String>
        get() = _products.value.map { it.category.name }.distinct()

    fun getFilteredProducts(): List<Product> {
        val category = _selectedCategory.value
        val allProducts = _products.value
        return if (category == null) allProducts
        else allProducts.filter { it.category.name == category }
    }

    //Obtener órdenes de un usuario específico
    fun getOrdersByUser(userId: Int): List<Order> {
        return _orders.value.filter { it.userId == userId }
    }

    //Obtener todas las órdenes
    fun getAllOrders(): List<Order> {
        return _orders.value
    }
}