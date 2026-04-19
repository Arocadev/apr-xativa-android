package com.example.aprxtiva.entities

data class Vehiculo(
    val id: Long = 0,
    val matricula: String = "",
    val usuarioId: Long = 0,
    val tipoAcred: String = "",
    val activo: Boolean = true,
    val createdAt: String = ""
)

data class VehiculoRequest(
    val matricula: String,
    val tipoAcred: String
)