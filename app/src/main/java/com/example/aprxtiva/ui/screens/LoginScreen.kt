package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import com.example.aprxtiva.viewmodel.LoginState

@Composable
fun LoginScreen(
    onLoginSuccess: (Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarMensajeContrasena by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()
    val t = IdiomaManager.textos

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            val activo = (loginState as LoginState.Success).activo
            onLoginSuccess(activo)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            listOf("val", "es", "en").forEach { idioma ->
                TextButton(
                    onClick = { IdiomaManager.idiomaActual = idioma },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (IdiomaManager.idiomaActual == idioma)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = idioma.uppercase(),
                        fontWeight = if (IdiomaManager.idiomaActual == idioma)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = t.loginTitulo,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = t.loginSubtitulo,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it.uppercase() },
            label = { Text(t.dni) },
            placeholder = { Text("12345678A") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(t.contrasena) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (loginState is LoginState.Error) {
            Text(
                text = (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        TextButton(
            onClick = { mostrarMensajeContrasena = !mostrarMensajeContrasena },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(t.olvidasteContrasena, fontSize = 13.sp)
        }

        if (mostrarMensajeContrasena) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = t.olvidasteContrasenaMsg,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(dni, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is LoginState.Loading
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(t.iniciarSesion)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text(t.noTienesCuenta)
        }
    }
}