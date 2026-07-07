package com.example.aprxtiva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprxtiva.entities.Vehiculo
import com.example.aprxtiva.repository.VehiculoRepository
import com.example.aprxtiva.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class VehiculoViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos

    private val _estado = MutableStateFlow<EstadoUI>(EstadoUI.Idle)
    val estado: StateFlow<EstadoUI> = _estado

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje: StateFlow<String?> = _errorMensaje

    fun limpiarError() { _errorMensaje.value = null }

    fun cargarVehiculos() {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: run {
                _estado.value = EstadoUI.Error("Sessió no vàlida")
                return@launch
            }
            val result = VehiculoRepository(token).getVehiculos()
            if (result.isSuccess) {
                _vehiculos.value = result.getOrNull() ?: emptyList()
                _estado.value = EstadoUI.Success
            } else {
                _estado.value = EstadoUI.Error(result.exceptionOrNull()?.message ?: "Error al carregar vehicles")
                _errorMensaje.value = result.exceptionOrNull()?.message ?: "Error al carregar vehicles"
            }
        }
    }

    fun addVehiculo(matricula: String, tipoAcred: String) {
        viewModelScope.launch {
            _estado.value = EstadoUI.Loading
            val token = tokenManager.token.first() ?: return@launch
            val result = VehiculoRepository(token).addVehiculo(matricula, tipoAcred)
            if (result.isSuccess) {
                cargarVehiculos()
            } else {
                _estado.value = EstadoUI.Success
                _errorMensaje.value = result.exceptionOrNull()?.message ?: "Error al afegir vehicle"
            }
        }
    }

    fun deleteVehiculo(id: Long) {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: return@launch
            val result = VehiculoRepository(token).deleteVehiculo(id)
            if (result.isSuccess) {
                cargarVehiculos()
            } else {
                _errorMensaje.value = result.exceptionOrNull()?.message ?: "Error al eliminar vehicle"
            }
        }
    }

    fun reactivarVehiculo(id: Long) {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: return@launch
            val result = VehiculoRepository(token).reactivarVehiculo(id)
            if (result.isSuccess) {
                cargarVehiculos()
            } else {
                _errorMensaje.value = result.exceptionOrNull()?.message ?: "Error al reactivar vehicle"
            }
        }
    }
}