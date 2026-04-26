package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.CambiarPasswordRequest
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.TokenManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val email by viewModel.email.collectAsState(initial = "")
    val rol by viewModel.rol.collectAsState(initial = "")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var exito by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.perfil, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t.volver)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dades del compte",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(text = "Email: ${email ?: ""}", fontSize = 14.sp)
                    Text(text = "${t.rol}: ${rol ?: ""}", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = t.cambiarContrasena,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = passwordActual,
                        onValueChange = { passwordActual = it },
                        label = { Text(t.contrasenaActual) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordNueva,
                        onValueChange = { passwordNueva = it },
                        label = { Text(t.contrasenaNueva) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordConfirm,
                        onValueChange = { passwordConfirm = it },
                        label = { Text(t.confirmarContrasena) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }
                    if (exito.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = exito, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            error = ""
                            exito = ""
                            if (passwordNueva != passwordConfirm) {
                                error = t.contrasenasCoincidenError
                                return@Button
                            }
                            scope.launch {
                                cargando = true
                                try {
                                    val token = TokenManager(context).token.first() ?: return@launch
                                    val api = RetrofitClient.getClient(token)
                                    val response = api.cambiarPassword(
                                        CambiarPasswordRequest(passwordActual, passwordNueva)
                                    )
                                    if (response.isSuccessful) {
                                        exito = t.contrasenaExito
                                        passwordActual = ""
                                        passwordNueva = ""
                                        passwordConfirm = ""
                                    } else {
                                        error = "Error al canviar la contrasenya"
                                    }
                                } catch (e: Exception) {
                                    error = t.errorConexion
                                } finally {
                                    cargando = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !cargando
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(t.cambiarContrasena)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        viewModel.logout()
                        onLogout()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(t.cerrarSesion)
            }
        }
    }
}