package com.example.kozi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        UserEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
}
