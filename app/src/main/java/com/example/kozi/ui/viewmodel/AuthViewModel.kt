package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.prefs.SessionStore
import com.example.kozi.data.remote.KoziApiClient
import com.example.kozi.data.remote.model.LoginRequest
import com.example.kozi.data.remote.model.RegisterRequest
import com.example.kozi.data.remote.model.RolRef
import com.example.kozi.data.remote.model.MembresiaRef
import com.example.kozi.data.remote.model.Usuario
import com.example.kozi.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthErrors(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null
)

data class AuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isVip: Boolean = false,
    val errors: AuthErrors = AuthErrors()
)

class AuthViewModel(
    private val session: SessionStore
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Usuarios locales + registro local (fallback)
    private val users = mutableListOf(
        User(1, "Dani", "dani@gmail.com", "123456", isVip = true),
        User(2, "Lucas", "lucas@gmail.com", "123456", isVip = false)
    )

    init {
        viewModelScope.launch {
            session.currentUser.collect { saved ->
                _currentUser.value = saved

                // Si el usuario guardado no existe en nuestra lista local, agregarlo
                if (saved != null && users.none { it.email == saved.email }) {
                    users.add(saved)
                }
            }
        }
    }


    /**
     * Mapea el Usuario del backend al User que usa la app.
     *
     * Usuario backend (según Swagger):
     *  - nombreUsuario
     *  - email
     *  - membresia.tipoMembresia = "VIP" / "NORMAL" ...
     */
    private fun usuarioToUser(remote: Usuario, plainPassword: String?): User {
        val tipoMembresia = remote.membresia?.tipoMembresia
        val isVip = tipoMembresia?.equals("VIP", ignoreCase = true) == true

        return User(
            id = remote.id.toInt(),              // si prefieres Long, cambia tu User
            name = remote.nombreUsuario,
            email = remote.email,
            password = plainPassword ?: "",
            isVip = isVip
        )
    }

    fun onNameChange(value: String) {
        _authState.update {
            it.copy(name = value, errors = it.errors.copy(name = null))
        }
    }

    fun onEmailChange(value: String) {
        _authState.update {
            it.copy(email = value, errors = it.errors.copy(email = null))
        }
    }

    fun onPasswordChange(value: String) {
        _authState.update {
            it.copy(password = value, errors = it.errors.copy(password = null))
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _authState.update {
            it.copy(confirmPassword = value, errors = it.errors.copy(confirmPassword = null))
        }
    }

    fun onVipChange(value: Boolean) {
        _authState.update { it.copy(isVip = value) }
    }

    private fun validateRegisterForm(state: AuthState): AuthErrors {
        var nameError: String? = null
        var emailError: String? = null
        var passError: String? = null
        var confirmError: String? = null

        if (state.name.isBlank()) nameError = "Ingresa tu nombre"

        if (state.email.isBlank()) {
            emailError = "Ingresa tu correo"
        } else if (!state.email.contains("@")) {
            emailError = "Correo inválido"
        }

        if (state.password.length < 4) {
            passError = "La contraseña debe tener mínimo 4 caracteres"
        }

        if (state.confirmPassword != state.password) {
            confirmError = "Las contraseñas no coinciden"
        }

        return AuthErrors(nameError, emailError, passError, confirmError)
    }

    private fun validateLoginForm(state: AuthState): AuthErrors {
        var emailError: String? = null
        var passError: String? = null

        if (state.email.isBlank()) emailError = "Ingresa tu correo"
        if (state.password.isBlank()) passError = "Ingresa tu contraseña"

        return AuthErrors(email = emailError, password = passError)
    }

    fun registerUser() {
        val state = _authState.value
        val errors = validateRegisterForm(state)

        if (listOfNotNull(
                errors.name, errors.email, errors.password, errors.confirmPassword
            ).isNotEmpty()
        ) {
            _authState.update { it.copy(errors = errors) }
            return
        }

        viewModelScope.launch {
            // Intentar registro en backend
            try {
                val rolId = 1L
                val membresiaId = if (state.isVip) 2L else 1L

                val request = RegisterRequest(
                    nombreUsuario = state.name,
                    email = state.email,
                    password = state.password,
                    rol = RolRef(id = rolId),
                    membresia = MembresiaRef(id = membresiaId)
                )

                val response = KoziApiClient.api.registerUsuario(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val user = usuarioToUser(body, state.password)

                        if (users.none { it.email == user.email }) {
                            users.add(user)
                        }

                        session.setCurrentUser(user)
                        _currentUser.value = user
                        _authState.value = AuthState() // limpiar formulario
                        return@launch
                    } else {
                        _authState.update {
                            it.copy(
                                errors = it.errors.copy(
                                    email = "Respuesta vacía del servidor"
                                )
                            )
                        }
                        return@launch
                    }
                } else {
                    val msg = if (response.code() == 400)
                        "Este correo ya está registrado en el servidor"
                    else
                        "Error al registrar (${response.code()})"

                    _authState.update {
                        it.copy(
                            errors = it.errors.copy(
                                email = msg
                            )
                        )
                    }
                    return@launch
                }
            } catch (e: Exception) {
                // Si el servidor está caído o sin conexión, seguimos al fallback
            }

            // Fallback: registro local
            if (users.any { it.email == state.email }) {
                _authState.update {
                    it.copy(
                        errors = it.errors.copy(
                            email = "Este correo ya está registrado (local)"
                        )
                    )
                }
                return@launch
            }

            val newUser = User(
                id = (users.maxOfOrNull { it.id } ?: 0) + 1,
                name = state.name,
                email = state.email,
                password = state.password,
                isVip = state.isVip
            )

            users.add(newUser)
            session.setCurrentUser(newUser)
            _currentUser.value = newUser
            _authState.value = AuthState()
        }
    }

    fun loginUser() {
        val state = _authState.value
        val errors = validateLoginForm(state)

        if (listOfNotNull(errors.email, errors.password).isNotEmpty()) {
            _authState.update { it.copy(errors = errors) }
            return
        }

        viewModelScope.launch {
            try {
                val request = LoginRequest(
                    email = state.email,
                    password = state.password
                )

                val response = KoziApiClient.api.loginUsuario(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val user = usuarioToUser(body, state.password)

                        if (users.none { it.email == user.email }) {
                            users.add(user)
                        }

                        session.setCurrentUser(user)
                        _currentUser.value = user
                        _authState.value = AuthState()
                        return@launch
                    }
                }
                // si no es exitoso, seguimos al fallback local
            } catch (e: Exception) {
                // error de red / servidor caído -> fallback local
            }

            // 2) Fallback: login local
            val user = users.find { it.email == state.email && it.password == state.password }
            if (user == null) {
                _authState.update {
                    it.copy(
                        errors = it.errors.copy(
                            email = "Correo o contraseña incorrectos",
                            password = "Correo o contraseña incorrectos"
                        )
                    )
                }
                return@launch
            }

            session.setCurrentUser(user)
            _currentUser.value = user
            _authState.value = AuthState()
        }
    }

    fun logout() {
        viewModelScope.launch {

            // 1. Borrar usuario
            session.setCurrentUser(null)

            // 2. Borrar tokens (por si más adelante usas JWT)
            session.saveTokens(null, null)

            // 3. Borrar email recordado
            session.rememberEmail(null)
        }

        _currentUser.value = null
        _authState.value = AuthState()
    }
}
