package com.example.kozi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val createdAt: Long,
    val subtotal: Double,
    val discount: Double,
    val total: Double
)