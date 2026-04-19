package com.example.aprxtiva.entities

data class DerechoAcceso(
    val id: Long = 0,
    val usuarioId: Long = 0,
    val vehiculoId: Long = 0,
    val matricula: String = "",
    val tipoDerecho: String = "",
    val tipoAcred: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val activo: Boolean = true
)

data class DerechoPermanenteRequest(
    val vehiculoId: Long,
    val tipoAcred: String,
    val fechaInicio: String,
    val fechaFin: String
)

data class DerechoPuntualRequest(
    val vehiculoId: Long,
    val tipoAcred: String,
    val fechaAcceso: String
)