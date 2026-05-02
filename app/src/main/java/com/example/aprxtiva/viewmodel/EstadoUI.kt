package com.example.aprxtiva.viewmodel

sealed class EstadoUI {
    object Idle : EstadoUI()
    object Loading : EstadoUI()
    data class Error(val message: String) : EstadoUI()
}