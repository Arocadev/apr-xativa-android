package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.ui.theme.TemaManager
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import com.example.aprxtiva.viewmodel.LoginState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    esLandscape: Boolean = false,
    viewModel: AuthViewModel = viewModel()
) {
    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarMensajeContrasena by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()
    val t = IdiomaManager.textos

    val oscuro = TemaManager.oscuro
    val colorFondo = if (oscuro) Color(0xFF1C1C1C) else Color(0xFFF8F7F5)
    val colorTexto = if (oscuro) Color.White else Color(0xFF2C2C2C)
    val colorSubtexto = if (oscuro) Color(0xFFAAAAAA) else Color.Gray
    val colorCard = if (oscuro) Color(0xFF2C2C2C) else Color(0xFFFFF3F3)

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
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
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = idioma.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = if (IdiomaManager.idiomaActual == idioma) FontWeight.Bold else FontWeight.Normal,
                            color = if (IdiomaManager.idiomaActual == idioma) Color(0xFFC0392B) else colorSubtexto
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = t.loginTitulo,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC0392B)
            )
            Text(
                text = t.loginSubtitulo,
                fontSize = 16.sp,
                color = colorSubtexto,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it.uppercase() },
                label = { Text(t.dni, color = colorSubtexto) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFC0392B),
                    unfocusedBorderColor = colorSubtexto,
                    focusedLabelColor = Color(0xFFC0392B),
                    unfocusedLabelColor = colorSubtexto,
                    cursorColor = Color(0xFFC0392B),
                    focusedTextColor = colorTexto,
                    unfocusedTextColor = colorTexto
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(t.contrasena, color = colorSubtexto) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFC0392B),
                    unfocusedBorderColor = colorSubtexto,
                    focusedLabelColor = Color(0xFFC0392B),
                    unfocusedLabelColor = colorSubtexto,
                    cursorColor = Color(0xFFC0392B),
                    focusedTextColor = colorTexto,
                    unfocusedTextColor = colorTexto
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (loginState is LoginState.Error) {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = Color(0xFFC0392B),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            TextButton(
                onClick = { mostrarMensajeContrasena = !mostrarMensajeContrasena },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    t.olvidasteContrasena,
                    fontSize = 13.sp,
                    color = Color(0xFFC0392B)
                )
            }

            if (mostrarMensajeContrasena) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorCard)
                ) {
                    Text(
                        text = t.olvidasteContrasenaMsg,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFC0392B),
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(dni, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                enabled = loginState !is LoginState.Loading
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        t.iniciarSesion,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    t.noTienesCuenta,
                    color = Color(0xFFC0392B),
                    fontSize = 14.sp
                )
            }
        }
    }
}