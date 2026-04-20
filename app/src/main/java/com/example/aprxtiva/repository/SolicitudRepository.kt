package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.Solicitud

class SolicitudRepository(private val token: String) {

    private val api = RetrofitClient.getClient(token)

    suspend fun crearSolicitud(): Result<Solicitud> {
        return try {
            val response = api.crearSolicitud()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear solicitud"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun getMisSolicitudes(): Result<List<Solicitud>> {
        return try {
            val response = api.getMisSolicitudes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar solicitudes"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}