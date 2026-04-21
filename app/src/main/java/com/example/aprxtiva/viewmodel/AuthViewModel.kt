package com.example.aprxtiva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprxtiva.repository.AuthRepository
import com.example.aprxtiva.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registroState = MutableStateFlow<RegistroState>(RegistroState.Idle)
    val registroState: StateFlow<RegistroState> = _registroState

    val token = tokenManager.token
    val email = tokenManager.email
    val rol = tokenManager.rol

    fun login(dni: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = repository.login(dni, password)
            if (result.isSuccess) {
                val response = result.getOrNull()!!
                tokenManager.guardarSesion(response.token, response.email, response.rol)
                _loginState.value = LoginState.Success(response.activo ?: true)
            } else {
                _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun registro(
        dni: String,
        nombre: String,
        apellidos: String,
        email: String,
        password: String,
        tipo: String
    ) {
        viewModelScope.launch {
            _registroState.value = RegistroState.Loading
            val result = repository.registro(dni, nombre, apellidos, email, password, tipo)
            if (result.isSuccess) {
                _registroState.value = RegistroState.Success
            } else {
                _registroState.value = RegistroState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.cerrarSesion()
            _loginState.value = LoginState.Idle
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val activo: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegistroState {
    object Idle : RegistroState()
    object Loading : RegistroState()
    object Success : RegistroState()
    data class Error(val message: String) : RegistroState()
}