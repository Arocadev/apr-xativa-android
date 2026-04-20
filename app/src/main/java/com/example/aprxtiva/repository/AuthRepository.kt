package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.LoginRequest
import com.example.aprxtiva.entities.LoginResponse
import com.example.aprxtiva.entities.RegistroRequest
import com.example.aprxtiva.entities.Usuario

class AuthRepository(private val token: String? = null) {

    private val api = RetrofitClient.getClient(token)

    suspend fun login(dni: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(dni, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("DNI o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun registro(
        dni: String,
        nombre: String,
        apellidos: String,
        email: String,
        password: String,
        tipo: String
    ): Result<Usuario> {
        return try {
            val response = api.registro(RegistroRequest(dni, nombre, apellidos, email, password, tipo))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al registrarse"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}