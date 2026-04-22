package com.example.aprxtiva.repository

import com.example.aprxtiva.api.RetrofitClient
import okhttp3.MultipartBody

class DocumentoRepository(private val token: String) {

    private val api = RetrofitClient.getClient(token)

    suspend fun subirDocumento(archivo: MultipartBody.Part): Result<Unit> {
        return try {
            val response = api.subirDocumento(archivo)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al subir documento"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
}