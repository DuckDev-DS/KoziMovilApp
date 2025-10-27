package com.example.kozi.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _profileImageUri = MutableStateFlow<android.net.Uri?>(null)
    val profileImageUri: StateFlow<android.net.Uri?> = _profileImageUri.asStateFlow()

    fun updateProfileImage(uri: android.net.Uri?) {
        _profileImageUri.value = uri
    }
}