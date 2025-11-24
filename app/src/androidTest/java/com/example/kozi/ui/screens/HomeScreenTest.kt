
package com.example.kozi.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kozi.R
import com.example.kozi.model.Category
import com.example.kozi.model.Product
import com.example.kozi.ui.viewmodel.CartViewModel
import com.example.kozi.ui.viewmodel.MainViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mocks para los ViewModels y el NavController
    private lateinit var navController: TestNavHostController
    private lateinit var mainViewModel: MainViewModel
    private lateinit var cartViewModel: CartViewModel

    // Datos de prueba que simularán la respuesta de la API/ViewModels
    private val fakeCategories = listOf("electronics", "jewelery")
    private val fakeProducts = listOf(
        Product(
            id = 1,
            name = "Laptop Pro",
            price = 1200.0,
            description = "A powerful laptop",
            image = R.drawable.ic_launcher_foreground,
            // --------------------------
            category = Category(id = 1, name = "electronics")
        ),
        Product(
            id = 2,
            name = "Diamond Ring",
            price = 2500.0,
            description = "A beautiful ring",
            image = R.drawable.ic_launcher_foreground,
            // --------------------------
            category = Category(id = 2, name = "jewelery")
        )
    )

    @Before
    fun setup() {
        //  Crear instancia del NavController de prueba
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        //  Mockear MainViewModel
        mainViewModel = mockk(relaxed = true)
        // Simular los flows y listas que usa HomeScreen
        every { mainViewModel.products } returns MutableStateFlow(fakeProducts)
        every { mainViewModel.categories } returns fakeCategories // Es una lista, no un Flow
        every { mainViewModel.selectedCategory } returns MutableStateFlow(null) // Por defecto, ninguna categoría seleccionada
        every { mainViewModel.showMessage } returns MutableStateFlow(null)

        // 3. Mockear CartViewModel (aunque no lo usemos activamente en estos tests, es necesario)
        cartViewModel = mockk(relaxed = true)
    }

    @Test
    fun homeScreen_displaysTitleAndDefaultCategoryFilter() {
        // Arrange: Cargar la UI con los mocks
        composeTestRule.setContent {
            HomeScreen(
                navController = navController,
                viewModel = mainViewModel,
                cartVm = cartViewModel
            )
        }

        // Assert: Verificar que el título y el filtro se muestran
        composeTestRule.onNodeWithText("kOzi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Todas las categorías").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysProducts_whenDataIsLoaded() {
        // Arrange
        composeTestRule.setContent {
            HomeScreen(
                navController = navController,
                viewModel = mainViewModel,
                cartVm = cartViewModel
            )
        }

        // Assert: Verificar que los productos de prueba aparecen en la pantalla
        composeTestRule.onNodeWithText("Laptop Pro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Diamond Ring").assertIsDisplayed()
    }
}
