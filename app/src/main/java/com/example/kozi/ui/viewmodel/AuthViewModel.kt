package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.prefs.SessionStore
import com.example.kozi.model.AuthErrors
import com.example.kozi.model.AuthState
import com.example.kozi.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val session: SessionStore   //inyección de DataStore
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    //Demo users
    private val users = mutableListOf<User>(
        User(1, "Dani", "dani@gmail.com", "123456", isVip = true),
        User(2, "Lucas","lucas@gmail.com","123456", isVip = false)
    )

    init {
        //Cargar usuario persistido al iniciar
        viewModelScope.launch {
            session.currentUser.collect { u ->
                _currentUser.value = u
            }
        }
    }

    //Registro
    fun registerUser(): Boolean {
        val state = _authState.value
        val errors = validateRegisterForm(state)
        _authState.update { it.copy(errors = errors) }

        val hasErrors = listOfNotNull(
            errors.name, errors.email, errors.password, errors.confirmPassword
        ).isNotEmpty()
        if (hasErrors) return false

        if (users.any { it.email == state.email }) {
            _authState.update { it.copy(errors = it.errors.copy(email = "Este correo ya está registrado")) }
            return false
        }

        val newUser = User(
            id = users.size + 1,
            name = state.name,
            email = state.email,
            password = state.password,
            isVip = state.isVip
        )
        users.add(newUser)

        //Persistir sesión
        viewModelScope.launch { session.setCurrentUser(newUser) }
        _currentUser.value = newUser
        return true
    }

    //Login
    fun loginUser(): Boolean {
        val state = _authState.value
        val errors = validateLoginForm(state)
        _authState.update { it.copy(errors = errors) }

        val hasErrors = listOfNotNull(errors.email, errors.password).isNotEmpty()
        if (hasErrors) return false

        val user = users.find { it.email == state.email && it.password == state.password }
        return if (user != null) {
            // 👇 Persistir sesión
            viewModelScope.launch { session.setCurrentUser(user) }
            _currentUser.value = user
            true
        } else {
            _authState.update {
                it.copy(errors = it.errors.copy(
                    email = "Correo o contraseña incorrectos",
                    password = "Correo o contraseña incorrectos"
                ))
            }
            false
        }
    }

    //Logout
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState()
        // 👇 Borrar sesión persistida
        viewModelScope.launch { session.setCurrentUser(null) }
    }

    fun getCurrentUser(): User? = _currentUser.value

    //Updates de campos
    fun onNameChange(v: String) { _authState.update { it.copy(name = v, errors = it.errors.copy(name = null)) } }
    fun onEmailChange(v: String){ _authState.update { it.copy(email = v, errors = it.errors.copy(email = null)) } }
    fun onPasswordChange(v: String){ _authState.update { it.copy(password = v, errors = it.errors.copy(password = null)) } }
    fun onConfirmPasswordChange(v: String){
        _authState.update { it.copy(confirmPassword = v, errors = it.errors.copy(confirmPassword = null)) }
    }
    fun onVipChange(v: Boolean){ _authState.update { it.copy(isVip = v) } }

    //Validaciones
    private fun validateRegisterForm(s: AuthState) = AuthErrors(
        name = if (s.name.isBlank()) "Nombre es obligatorio" else null,
        email = if (s.email.isBlank()) "Correo es obligatorio" else if (!s.email.contains("@")) "Correo inválido" else null,
        password = if (s.password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null,
        confirmPassword = if (s.password != s.confirmPassword) "Las contraseñas no coinciden" else null
    )
    private fun validateLoginForm(s: AuthState) = AuthErrors(
        email = if (s.email.isBlank()) "Correo es obligatorio" else null,
        password = if (s.password.isBlank()) "Contraseña es obligatoria" else null
    )
}
