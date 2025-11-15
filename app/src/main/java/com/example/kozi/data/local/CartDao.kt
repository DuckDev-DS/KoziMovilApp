package com.example.kozi.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun observeCart(): Flow<List<CartItemEntity>>

    // Lectura directa para checkout
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    suspend fun getCart(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :qty WHERE id = :id")
    suspend fun updateQty(id: Long, qty: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}
