package com.example.kozi.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kozi.data.local.CartItemEntity
import com.example.kozi.model.User
import com.example.kozi.ui.viewmodel.AuthViewModel
import com.example.kozi.ui.viewmodel.CartViewModel
import com.example.kozi.ui.viewmodel.OrderViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartScreenTitleTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cartScreen_showsTitleMiCarrito() {
        // ViewModels mockeados, no usan DAOs reales ni SessionStore
        val cartVm = mockk<CartViewModel>(relaxed = true)
        val authVm = mockk<AuthViewModel>(relaxed = true)
        val orderVm = mockk<OrderViewModel>(relaxed = true)

        // Estado del carrito vacío
        val itemsFlow = MutableStateFlow<List<CartItemEntity>>(emptyList())
        every { cartVm.items } returns itemsFlow

        // Usuario no autenticado
        val userFlow = MutableStateFlow<User?>(null)
        every { authVm.currentUser } returns userFlow

        composeRule.setContent {
            CartScreen(
                navController = null,
                cartVm = cartVm,
                authViewModel = authVm,
                orderVm = orderVm
            )
        }

        // Verifica que el título aparece
        composeRule.onNodeWithText("Mi Carrito").assertIsDisplayed()
    }
}


//SON LAS 6 AM, NOSE QUE HAGO, NO SE QUE SOY, TENGO MAS CAFE EN MIS VENAS QUE SANGRE,
//TENGO QUE ENTREGAR ESTO A LAS 9
//LLEVO 3 HORAS INTENTANDO HACER LOS TEST
//Y NO PUEDO TESTEAR NI UN MALDITO TITULO
//AYUDAAAA
//ODIO MI VIDA
//VIVA JESUCRISTO
//ABAJO LAS DEPENDENCIAS!!!!!