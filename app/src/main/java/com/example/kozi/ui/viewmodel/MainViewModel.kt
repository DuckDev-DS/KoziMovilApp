package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.R
import com.example.kozi.data.remote.KoziApiClient
import com.example.kozi.data.remote.model.Producto as RemoteProducto
import com.example.kozi.model.Category
import com.example.kozi.model.Order
import com.example.kozi.model.Product
import com.example.kozi.model.User
import com.example.kozi.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

<<<<<<< HEAD
    // Lista de productos que consumen las pantallas
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Carrito (Product -> cantidad) para este ViewModel
    private val _cartItems = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cartItems: StateFlow<Map<Product, Int>> = _cartItems.asStateFlow()

    // Filtro de categoría seleccionada
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Mensajes (snackbar)
=======
    // Lista de productos que consumen tus pantallas
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Carrito (Product -> cantidad)
    private val _cartItems = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cartItems: StateFlow<Map<Product, Int>> = _cartItems.asStateFlow()

    // Filtro por categoría
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Mensajes (snackbar en HomeScreen)
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
    private val _showMessage = MutableStateFlow<String?>(null)
    val showMessage: StateFlow<String?> = _showMessage.asStateFlow()

    // Historial de órdenes
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        viewModelScope.launch {
<<<<<<< HEAD
            // 1) Productos locales (para no dejar la app vacía si la API falla)
            val localProducts = ProductRepository.getProducts()
            _products.value = localProducts

            // 2) Intentar cargar datos desde la API
=======
            // 1) Cargar productos locales (categorías + imágenes) para no dejar la app en blanco
            val localProducts = ProductRepository.getProducts()
            _products.value = localProducts

            // 2) Intentar actualizar con datos reales desde la API
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
            try {
                val remoteProducts = KoziApiClient.api.getProductos()
                val merged = mergeRemoteWithLocal(remoteProducts, localProducts)
                _products.value = merged
            } catch (e: Exception) {
<<<<<<< HEAD
                // Si algo falla, nos quedamos con los productos locales
=======
                // Si la API falla, seguimos con productos locales y mostramos un mensaje
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
                _showMessage.value =
                    "No se pudo conectar con el servidor. Se muestran productos locales."
            }
        }
    }

    /**
     * Une los productos remotos con los locales:
     * - Usa nombre, descripción y precio del backend.
<<<<<<< HEAD
     * - Mantiene categoría e imagen de tu repositorio local (para que tus pantallas no cambien).
=======
     * - Mantiene categoría e imagen de tu repositorio local (para que tus screens no cambien).
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
     */
    private fun mergeRemoteWithLocal(
        remote: List<RemoteProducto>,
        local: List<Product>
    ): List<Product> {
        val localById = local.associateBy { it.id }
        val defaultCategory: Category =
            ProductRepository.categories.firstOrNull() ?: Category(0, "Otros")

        val fromRemote = remote.map { p ->
            val base = localById[p.id.toInt()]
            if (base != null) {
                base.copy(
                    name = p.nombre,
                    description = p.descripcion,
                    price = p.precio
                )
            } else {
<<<<<<< HEAD
                // Producto que está en la API pero no en el repo local
=======
                // Si llega un producto que no existe en el repo local,
                // lo creamos con categoría por defecto e imagen genérica.
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
                Product(
                    id = p.id.toInt(),
                    name = p.nombre,
                    description = p.descripcion,
                    price = p.precio,
                    category = defaultCategory,
<<<<<<< HEAD
                    image = R.drawable.ic_launcher_foreground // genérico
=======
                    image = R.drawable.ic_launcher_foreground
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
                )
            }
        }

        // Opcional: agregar productos que existen solo localmente y no en la API
        val remoteIds = remote.map { it.id.toInt() }.toSet()
        val onlyLocal = local.filter { it.id !in remoteIds }

        return fromRemote + onlyLocal
    }

    fun getTotalPriceWithDiscount(isVip: Boolean): Double {
        val total = getTotalPrice()
        return if (isVip) total * 0.8 else total // 20% de descuento para VIP
    }

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

<<<<<<< HEAD
    // Guarda en historial antes de limpiar
=======
    // Guarda la compra en el historial y limpia el carrito
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
    fun clearCart(currentUser: User? = null) {
        val cartItemsCurrent = _cartItems.value
        if (cartItemsCurrent.isNotEmpty() && currentUser != null) {
            val newOrder = Order(
                id = "ORDER_${System.currentTimeMillis()}",
                userId = currentUser.id,
                products = cartItemsCurrent.toMap(),
                total = getTotalPriceWithDiscount(currentUser.isVip),
                discount = getDiscountAmount(currentUser.isVip),
                isVip = currentUser.isVip
            )
            _orders.value = _orders.value + newOrder
        }

        _cartItems.value = emptyMap()
        _showMessage.value = "🎉 ¡Compra realizada con éxito!"
    }

    fun clearMessage() {
        _showMessage.value = null
    }

<<<<<<< HEAD
    // Lista de nombres de categoría (para el dropdown de Home)
=======
    // Lista de nombres de categoría (para filtros)
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
    val categories: List<String>
        get() = _products.value.map { it.category.name }.distinct()

    fun getFilteredProducts(): List<Product> {
        val category = _selectedCategory.value
        val allProducts = _products.value
        return if (category == null) allProducts
        else allProducts.filter { it.category.name == category }
    }

<<<<<<< HEAD
    // Obtener órdenes de un usuario específico
=======
    // Historial por usuario
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
    fun getOrdersByUser(userId: Int): List<Order> {
        return _orders.value.filter { it.userId == userId }
    }

<<<<<<< HEAD
    // Obtener todas las órdenes
=======
    // Todas las órdenes
>>>>>>> f6cf1d074172c6631562fb2584b9f1c5c1fe51d8
    fun getAllOrders(): List<Order> {
        return _orders.value
    }
}
