package com.example.kozi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val correo: String,
    val tipoMembresia: String?,
    val activo: Boolean,
    val rolId: Long?,
    val rolNombre: String?,
    // Campos extra
    val photoUri: String? = null,
    val isVip: Boolean = false,
    val password: String? = null // NO se usará para login real, pero mantiene compatibilidad por ahora
)