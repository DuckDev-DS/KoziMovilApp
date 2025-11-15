package com.example.kozi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Int,
    val name: String,
    val price: Double,
    val imageRes: Int?,
    val quantity: Int
)
