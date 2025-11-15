package com.example.kozi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 2, //sube la versión
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
}
