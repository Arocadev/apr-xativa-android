package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.LoginRequest
import com.example.aprxtiva.entities.LoginResponse
import com.example.aprxtiva.entities.RegistroRequest
import com.example.aprxtiva.entities.Usuario
import org.json.JSONObject

class AuthRepository {

    private val api = RetrofitClient.getClient()

    suspend fun login(dni: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(dni, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val mensaje = try {
                    val json = JSONObject(response.errorBody()?.string() ?: "")
                    json.getString("mensaje")
                } catch (e: Exception) {
                    "DNI o contrasenya incorrectes"
                }
                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de connexió"))
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
                val mensaje = try {
                    val json = JSONObject(response.errorBody()?.string() ?: "")
                    json.getString("mensaje")
                } catch (e: Exception) {
                    "Error al registrar-se"
                }
                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de connexió"))
        }
    }
}