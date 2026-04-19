package com.example.aprxtiva.entities

data class Usuario(
    val id: Long = 0,
    val dni: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val rol: String = "",
    val tipo: String = "",
    val activo: Boolean = true
)

data class LoginRequest(
    val dni: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val email: String,
    val rol: String
)

data class RegistroRequest(
    val dni: String,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val password: String,
    val tipo: String
)