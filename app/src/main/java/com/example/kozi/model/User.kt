package com.example.kozi.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val photoUri: String? = null,
    val isVip: Boolean = false
)