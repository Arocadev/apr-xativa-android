package com.example.aprxtiva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprxtiva.entities.Solicitud
import com.example.aprxtiva.repository.SolicitudRepository
import com.example.aprxtiva.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SolicitudViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    private val _solicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudes: StateFlow<List<Solicitud>> = _solicitudes

    private val _estado = MutableStateFlow<EstadoUI>(EstadoUI.Idle)
    val estado: StateFlow<EstadoUI> = _estado

    fun cargarSolicitudes() {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: return@launch
            val result = SolicitudRepository(token).getMisSolicitudes()
            if (result.isSuccess) {
                _solicitudes.value = result.getOrNull() ?: emptyList()
                _estado.value = EstadoUI.Idle
            } else {
                _estado.value = EstadoUI.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun crearSolicitud() {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: return@launch
            val result = SolicitudRepository(token).crearSolicitud()
            if (result.isSuccess) {
                cargarSolicitudes()
            } else {
                _estado.value = EstadoUI.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}