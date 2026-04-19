package com.example.aprxtiva.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenManager(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val EMAIL_KEY = stringPreferencesKey("email")
        val ROL_KEY = stringPreferencesKey("rol")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val rol: Flow<String?> = context.dataStore.data.map { it[ROL_KEY] }

    suspend fun guardarSesion(token: String, email: String, rol: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[EMAIL_KEY] = email
            it[ROL_KEY] = rol
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(EMAIL_KEY)
            it.remove(ROL_KEY)
        }
    }
}