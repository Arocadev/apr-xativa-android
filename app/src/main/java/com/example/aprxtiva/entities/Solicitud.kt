package com.example.aprxtiva.entities

data class Solicitud(
    val id: Long = 0,
    val usuarioId: Long = 0,
    val estado: String = "",
    val observaciones: String? = null,
    val createdAt: String = "",
    val gestionadaAt: String? = null
)