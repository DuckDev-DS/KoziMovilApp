package com.example.kozi.data.mapper

import com.example.kozi.data.local.UserEntity
import com.example.kozi.data.remote.model.Usuario
import com.example.kozi.model.User


    fun Usuario.toEntity(
    currentPhotoUri: String? = null,
    currentPassword: String? = null
): UserEntity = UserEntity(
    id = id,
    nombre = nombreUsuario,
    correo = email,
    tipoMembresia = membresia?.tipoMembresia,
    activo = activo,
    rolId = rol?.id,
    rolNombre = rol?.nombreRol,
    photoUri = currentPhotoUri,
    isVip = membresia?.tipoMembresia
        ?.equals("VIP", ignoreCase = true) == true,
    password = currentPassword
)



fun UserEntity.toDomain(): User = User(
    id = id.toInt(),
    name = nombre,
    email = correo,
    password = password ?: "",
    photoUri = photoUri,
    isVip = isVip
)


fun User.toEntity(): UserEntity = UserEntity(
    id = this.id.toLong(),
    nombre = this.name,
    correo = this.email,
    tipoMembresia = if (this.isVip) "VIP" else "NORMAL",
    activo = true,
    rolId = null,
    rolNombre = null,
    photoUri = this.photoUri,
    isVip = this.isVip,
    password = this.password
)
