package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.Vehiculo
import com.example.aprxtiva.entities.VehiculoRequest

class VehiculoRepository(private val token: String) {

    private val api = RetrofitClient.getClient(token)

    suspend fun getVehiculos(): Result<List<Vehiculo>> {
        return try {
            val response = api.getVehiculos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar vehículos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun addVehiculo(matricula: String, tipoAcred: String): Result<Vehiculo> {
        return try {
            val response = api.addVehiculo(VehiculoRequest(matricula, tipoAcred))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al añadir vehículo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    suspend fun deleteVehiculo(id: Long): Result<Unit> {
        return try {
            val response = api.deleteVehiculo(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar vehículo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}