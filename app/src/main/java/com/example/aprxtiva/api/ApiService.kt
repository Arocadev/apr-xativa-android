package com.example.aprxtiva.api

import com.example.aprxtiva.entities.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/usuarios/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<Usuario>

    // Usuarios
    @GET("api/usuarios/me")
    suspend fun getMe(): Response<Usuario>

    @PUT("api/usuarios/cambiar-password")
    suspend fun cambiarPassword(@Body request: CambiarPasswordRequest): Response<Void>

    // Vehículos
    @GET("api/vehiculos")
    suspend fun getVehiculos(): Response<List<Vehiculo>>

    @POST("api/vehiculos")
    suspend fun addVehiculo(@Body request: VehiculoRequest): Response<Vehiculo>

    @DELETE("api/vehiculos/{id}")
    suspend fun deleteVehiculo(@Path("id") id: Long): Response<Void>

    // Solicitudes
    @POST("api/solicitudes")
    suspend fun crearSolicitud(): Response<Solicitud>

    @GET("api/solicitudes/me")
    suspend fun getMisSolicitudes(): Response<List<Solicitud>>

    @GET("api/solicitudes/estado")
    suspend fun getEstadoSolicitud(): Response<Solicitud>

    // Derechos
    @GET("api/derechos")
    suspend fun getMisDerechos(): Response<List<DerechoAcceso>>

    @POST("api/derechos/permanente")
    suspend fun crearDerechoPermanente(@Body request: DerechoPermanenteRequest): Response<DerechoAcceso>

    @POST("api/derechos/puntual")
    suspend fun crearDerechoPuntual(@Body request: DerechoPuntualRequest): Response<DerechoAcceso>

    @DELETE("api/derechos/{id}")
    suspend fun deleteDerechoAcceso(@Path("id") id: Long): Response<Void>
}