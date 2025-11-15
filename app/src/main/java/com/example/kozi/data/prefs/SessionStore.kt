package com.example.kozi.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.kozi.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session_prefs")

private object Keys {
    val EMAIL = stringPreferencesKey("user_email")
    val NAME = stringPreferencesKey("user_name")
    val VIP = booleanPreferencesKey("user_is_vip")

    //Claves adicionales
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val REMEMBERED_EMAIL = stringPreferencesKey("remembered_email")
}

class SessionStore(private val context: Context) {

    //Usuario actual persistido
    val currentUser: Flow<User?> = context.dataStore.data.map { p ->
        val email = p[Keys.EMAIL] ?: return@map null
        User(
            id = 0,
            name = p[Keys.NAME] ?: "",
            email = email,
            password = "",
            photoUri = null,
            isVip = p[Keys.VIP] ?: false
        )
    }

    suspend fun setCurrentUser(user: User?) {
        context.dataStore.edit { p ->
            if (user == null) {
                p.remove(Keys.EMAIL); p.remove(Keys.NAME); p.remove(Keys.VIP)
            } else {
                p[Keys.EMAIL] = user.email
                p[Keys.NAME] = user.name
                p[Keys.VIP] = user.isVip
            }
        }
    }

    //Tokens y correo recordado
    val accessToken: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val rememberedEmail: Flow<String?> = context.dataStore.data.map { it[Keys.REMEMBERED_EMAIL] }

    suspend fun saveTokens(access: String?, refresh: String?) {
        context.dataStore.edit { p ->
            if (access == null && refresh == null) {
                p.remove(Keys.ACCESS_TOKEN)
                p.remove(Keys.REFRESH_TOKEN)
            } else {
                if (access != null) p[Keys.ACCESS_TOKEN] = access
                if (refresh != null) p[Keys.REFRESH_TOKEN] = refresh
            }
        }
    }

    suspend fun rememberEmail(email: String?) {
        context.dataStore.edit { p ->
            if (email == null) p.remove(Keys.REMEMBERED_EMAIL)
            else p[Keys.REMEMBERED_EMAIL] = email
        }
    }
}
