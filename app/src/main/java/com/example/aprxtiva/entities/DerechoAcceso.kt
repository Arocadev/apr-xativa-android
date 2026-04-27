package com.example.aprxtiva.entities

data class DerechoAcceso(
    val id: Long = 0,
    val usuarioId: Long = 0,
    val vehiculoId: Long? = null,
    val matricula: String = "",
    val matriculaInvitado: String? = null,
    val tipoDerecho: String = "",
    val tipoAcred: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val activo: Boolean = true,
    val createdAt: String = ""
)