package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.DerechoAcceso
import com.example.aprxtiva.entities.DerechoPermanenteRequest
import com.example.aprxtiva.entities.DerechoPuntualRequest

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

    suspend fun crearDerechoPermanente(
        vehiculoId: Long,
        tipoAcred: String,
        fechaInicio: String,
        fechaFin: String
    ): Result<DerechoAcceso> {
        return try {
            val response = api.crearDerechoPermanente(
                DerechoPermanenteRequest(vehiculoId, tipoAcred, fechaInicio, fechaFin)
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

    suspend fun crearDerechoPuntual(
        vehiculoId: Long,
        tipoAcred: String,
        fechaAcceso: String
    ): Result<DerechoAcceso> {
        return try {
            val response = api.crearDerechoPuntual(
                DerechoPuntualRequest(vehiculoId, tipoAcred, fechaAcceso)
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