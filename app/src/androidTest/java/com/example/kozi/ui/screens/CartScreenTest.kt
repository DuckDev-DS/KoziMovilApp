// Ruta: app/src/androidTest/java/com/example/kozi/ui/screens/CartScreenTest.kt

package com.example.kozi.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController // Importa el NavController de prueba
import androidx.test.core.app.ApplicationProvider // Importa el contexto de la aplicación
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kozi.data.local.CartItemEntity
import com.example.kozi.model.User
import com.example.kozi.ui.viewmodel.AuthViewModel
import com.example.kozi.ui.viewmodel.CartViewModel
import com.example.kozi.ui.viewmodel.OrderViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before // Lo usaremos para configurar el NavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartScreenTitleTest {

    @get:Rule
    val composeRule = createComposeRule()

    //NavController de prueba aquí para que esté disponible en todos los tests
    private lateinit var navController: TestNavHostController

    //Este bloque se ejecuta antes de cada @Test
    @Before
    fun setup() {
        // 1. Crea una instancia del NavController de prueba
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext() // Necesita el contexto de la app
        )
    }

    @Test
    fun cartScreen_showsTitleMiCarrito() {
        // ViewModels mockeados
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
                //Pasa el NavController de prueba en lugar de null!
                navController = navController,
                cartVm = cartVm,
                authViewModel = authVm,
                orderVm = orderVm
            )
        }

        // Verifica que el título aparece (esto debería funcionar ahora)
        composeRule.onNodeWithText("Mi Carrito").assertIsDisplayed()
    }
}
