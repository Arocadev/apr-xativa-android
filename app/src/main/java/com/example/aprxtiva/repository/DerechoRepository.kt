package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.DerechoAcceso

class DerechoRepository(private val token: String) {

    private val api = RetrofitClient.getClient(token)

    suspend fun getMisDerechos(): Result<List<DerechoAcceso>> {
        return try {
            val response = api.getMisDerechos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar derechos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
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
                Result.failure(Exception("Error al crear derecho permanente"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
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
                Result.failure(Exception("Error al crear derecho puntual"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun deleteDerechoAcceso(id: Long): Result<Unit> {
        return try {
            val response = api.deleteDerechoAcceso(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar derecho"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}