package com.example.kozi.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: Category,
    val image: Int
)