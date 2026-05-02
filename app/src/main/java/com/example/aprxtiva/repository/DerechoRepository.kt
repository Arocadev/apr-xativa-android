package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.DerechoAcceso
import org.json.JSONObject

class DerechoRepository(private val token: String) {

    private val api = RetrofitClient.getClient(token)

    private fun parsearError(errorBody: okhttp3.ResponseBody?): String {
        return try {
            val json = JSONObject(errorBody?.string() ?: "")
            json.optString("mensaje", "Error desconocido")
        } catch (e: Exception) {
            "Error desconocido"
        }
    }

    suspend fun getMisDerechos(): Result<List<DerechoAcceso>> {
        return try {
            val response = api.getMisDerechos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parsearError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de connexió"))
        }
    }

    suspend fun crearDerechoPermanente(vehiculoId: Long): Result<DerechoAcceso> {
        return try {
            val response = api.crearDerechoPermanente(
                mapOf("vehiculoId" to vehiculoId.toString())
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parsearError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de connexió"))
        }
    }

    suspend fun crearDerechoPuntualInvitado(
        matricula: String,
        fecha: String
    ): Result<DerechoAcceso> {
        return try {
            val response = api.crearDerechoPuntualInvitado(
                mapOf("matricula" to matricula, "fecha" to fecha)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parsearError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de connexió"))
        }
    }

    suspend fun deleteDerechoAcceso(id: Long): Result<Unit> {
        return try {
            val response = api.deleteDerechoAcceso(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parsearError(response.errorBody())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de connexió"))
        }
    }
}