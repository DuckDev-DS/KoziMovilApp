package com.example.kozi.data.repository

import com.example.kozi.data.local.UserDao
import com.example.kozi.data.mapper.toDomain
import com.example.kozi.data.mapper.toEntity
import com.example.kozi.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val userDao: UserDao
) {

    /**
     * Observa el usuario actual guardado en Room.
     * Si no hay usuario logueado, emite null.
     */
    fun observeCurrentUser(): Flow<User?> =
        userDao.observeUser().map { entity ->
            entity?.toDomain()
        }

    /**
     * Guarda/actualiza el usuario localmente (Room).
     * Esto se usará cuando:
     * - Se loguee correctamente
     * - Se actualice el perfil
     * - Se sincronicen datos desde el backend
     */
    suspend fun saveLocalUser(user: User) {
        userDao.upsert(user.toEntity())
    }

    /**
     * Borra al usuario local (por ejemplo al cerrar sesión).
     */
    suspend fun clearLocalUser() {
        userDao.clear()
    }

}