package com.example.kozi.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Transaction
    @Query("SELECT * FROM orders WHERE userEmail = :email ORDER BY createdAt DESC")
    fun observeOrdersFor(email: String): Flow<List<OrderWithItems>>
}
