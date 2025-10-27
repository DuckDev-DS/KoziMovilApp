package com.example.kozi.model

data class Order(
    val id: String,
    val userId: Int,
    val products: Map<Product, Int>,
    val total: Double,
    val discount: Double,
    val isVip: Boolean
)