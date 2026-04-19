package com.example.aprxtiva.entities

data class CambiarPasswordRequest(
    val passwordActual: String,
    val passwordNueva: String
)