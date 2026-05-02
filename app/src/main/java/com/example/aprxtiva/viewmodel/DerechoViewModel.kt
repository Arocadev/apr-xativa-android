package com.example.aprxtiva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprxtiva.entities.DerechoAcceso
import com.example.aprxtiva.repository.DerechoRepository
import com.example.aprxtiva.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DerechoViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    private val _derechos = MutableStateFlow<List<DerechoAcceso>>(emptyList())
    val derechos: StateFlow<List<DerechoAcceso>> = _derechos

    private val _estado = MutableStateFlow<EstadoUI>(EstadoUI.Idle)
    val estado: StateFlow<EstadoUI> = _estado

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje: StateFlow<String?> = _errorMensaje

    fun limpiarError() { _errorMensaje.value = null }

    fun cargarDerechos() {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: return@launch
            val result = DerechoRepository(token).getMisDerechos()
            if (result.isSuccess) {
                _derechos.value = result.getOrNull() ?: emptyList()
                _estado.value = EstadoUI.Idle
            } else {
                _estado.value = EstadoUI.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun crearDerechoPermanente(vehiculoId: Long) {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: return@launch
            val result = DerechoRepository(token).crearDerechoPermanente(vehiculoId)
            if (result.isSuccess) {
                cargarDerechos()
            } else {
                _estado.value = EstadoUI.Idle
                _errorMensaje.value = result.exceptionOrNull()?.message ?: "Error"
            }
        }
    }

    fun crearDerechoPuntualInvitado(matricula: String, fecha: String) {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: return@launch
            val result = DerechoRepository(token).crearDerechoPuntualInvitado(matricula, fecha)
            if (result.isSuccess) {
                cargarDerechos()
            } else {
                _estado.value = EstadoUI.Idle
                _errorMensaje.value = result.exceptionOrNull()?.message ?: "Error"
            }
        }
    }

    fun deleteDerechoAcceso(id: Long) {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: return@launch
            val result = DerechoRepository(token).deleteDerechoAcceso(id)
            if (result.isSuccess) {
                cargarDerechos()
            } else {
                _estado.value = EstadoUI.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}