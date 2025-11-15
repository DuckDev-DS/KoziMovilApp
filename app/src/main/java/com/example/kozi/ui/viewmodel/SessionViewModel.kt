package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kozi.data.prefs.SessionStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(private val prefs: SessionStore) : ViewModel() {

    // Observa tokens y email recordado
    val accessToken: StateFlow<String?> =
        prefs.accessToken.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val rememberedEmail: StateFlow<String?> =
        prefs.rememberedEmail.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Guarda tokens tras login exitoso
    fun saveTokens(access: String?, refresh: String?) = viewModelScope.launch {
        prefs.saveTokens(access, refresh)
    }

    // Guarda email recordado
    fun rememberEmail(email: String?) = viewModelScope.launch {
        prefs.rememberEmail(email)
    }

    // Limpia todo (logout)
    fun logout() = viewModelScope.launch {
        prefs.saveTokens(null, null)
    }
}
