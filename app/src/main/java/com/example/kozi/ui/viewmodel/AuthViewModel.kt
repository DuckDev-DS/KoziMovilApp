package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.model.AuthState
import com.example.kozi.model.AuthErrors
import com.example.kozi.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Al iniciar, si ya hay usuario autenticado, cargarlo desde Firebase
        auth.currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                val snapshot = db.child("users").child(firebaseUser.uid).get().await()
                val user = snapshot.getValue(User::class.java)
                _currentUser.value = user
            }
        }
    }

    // Registrar usuario en Firebase Auth + Database
    fun registerUser() {
        val state = _authState.value
        val errors = validateRegisterForm(state)
        _authState.update { it.copy(errors = errors) }

        val hasErrors = listOfNotNull(
            errors.name,
            errors.email,
            errors.password,
            errors.confirmPassword
        ).isNotEmpty()

        if (hasErrors) return

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(state.email, state.password).await()
                val firebaseUser = result.user ?: return@launch

                val newUser = User(
                    id = firebaseUser.uid.hashCode(),
                    name = state.name,
                    email = state.email,
                    password = state.password,
                    isVip = state.isVip
                )

                // Guardar en Realtime Database
                db.child("users").child(firebaseUser.uid).setValue(newUser).await()
                _currentUser.value = newUser

            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        errors = it.errors.copy(
                            email = "Error al registrar: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    // Iniciar sesión con Firebase Auth
    fun loginUser() {
        val state = _authState.value
        val errors = validateLoginForm(state)
        _authState.update { it.copy(errors = errors) }

        val hasErrors = listOfNotNull(errors.email, errors.password).isNotEmpty()
        if (hasErrors) return

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(state.email, state.password).await()
                val firebaseUser = result.user ?: return@launch

                val snapshot = db.child("users").child(firebaseUser.uid).get().await()
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    _currentUser.value = user
                } else {
                    _authState.update {
                        it.copy(errors = it.errors.copy(email = "Usuario no encontrado"))
                    }
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        errors = it.errors.copy(
                            email = "Correo o contraseña incorrectos",
                            password = "Correo o contraseña incorrectos"
                        )
                    )
                }
            }
        }
    }

    // Cerrar sesión
    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState()
    }

    fun getCurrentUser(): User? = _currentUser.value

    // Cambios en campos del formulario
    fun onNameChange(value: String) {
        _authState.update { it.copy(name = value, errors = it.errors.copy(name = null)) }
    }

    fun onEmailChange(value: String) {
        _authState.update { it.copy(email = value, errors = it.errors.copy(email = null)) }
    }

    fun onPasswordChange(value: String) {
        _authState.update { it.copy(password = value, errors = it.errors.copy(password = null)) }
    }

    fun onConfirmPasswordChange(value: String) {
        _authState.update { it.copy(confirmPassword = value, errors = it.errors.copy(confirmPassword = null)) }
    }

    fun onVipChange(value: Boolean) {
        _authState.update { it.copy(isVip = value) }
    }

    // Validaciones
    private fun validateRegisterForm(state: AuthState): AuthErrors {
        return AuthErrors(
            name = if (state.name.isBlank()) "Nombre es obligatorio" else null,
            email = when {
                state.email.isBlank() -> "Correo es obligatorio"
                !state.email.contains("@") -> "Correo inválido"
                else -> null
            },
            password = if (state.password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null,
            confirmPassword = if (state.password != state.confirmPassword) "Las contraseñas no coinciden" else null
        )
    }

    private fun validateLoginForm(state: AuthState): AuthErrors {
        return AuthErrors(
            email = if (state.email.isBlank()) "Correo es obligatorio" else null,
            password = if (state.password.isBlank()) "Contraseña es obligatoria" else null
        )
    }
}
