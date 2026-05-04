package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.entities.CambiarPasswordRequest
import com.example.aprxtiva.ui.theme.TemaManager
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.TokenManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val email by viewModel.email.collectAsState(initial = "")
    val perfilDni by viewModel.perfilDni.collectAsState()
    val perfilNombre by viewModel.perfilNombre.collectAsState()
    val perfilApellidos by viewModel.perfilApellidos.collectAsState()
    val perfilTipo by viewModel.perfilTipo.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val oscuro = TemaManager.oscuro
    val colorFondo = if (oscuro) Color(0xFF1C1C1C) else Color(0xFFF8F7F5)
    val colorTexto = if (oscuro) Color.White else Color(0xFF2C2C2C)
    val colorSubtexto = if (oscuro) Color(0xFFAAAAAA) else Color.Gray
    val colorCard = if (oscuro) Color(0xFF2C2C2C) else Color.White

    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var mostrarPasswordActual by remember { mutableStateOf(false) }
    var mostrarPasswordNueva by remember { mutableStateOf(false) }
    var mostrarPasswordConfirm by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var exito by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.cargarPerfil(context) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFC0392B),
        unfocusedBorderColor = colorSubtexto,
        focusedLabelColor = Color(0xFFC0392B),
        unfocusedLabelColor = colorSubtexto,
        cursorColor = Color(0xFFC0392B),
        focusedTextColor = colorTexto,
        unfocusedTextColor = colorTexto
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.perfil, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t.volver)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC0392B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFFFF0EE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color(0xFFC0392B))
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (perfilNombre.isNotEmpty()) {
                Text(text = "$perfilNombre $perfilApellidos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorTexto)
            }
            Text(text = email ?: "", fontSize = 14.sp, color = colorSubtexto)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = t.dadesCompte, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC0392B), modifier = Modifier.padding(bottom = 12.dp))
                    if (perfilDni.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = t.dni, fontSize = 14.sp, color = colorSubtexto)
                            Text(text = perfilDni, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorTexto)
                        }
                        HorizontalDivider(color = colorSubtexto.copy(alpha = 0.2f))
                    }
                    if (perfilTipo.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = t.tipo, fontSize = 14.sp, color = colorSubtexto)
                            Text(text = perfilTipo, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorTexto)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = t.cambiarContrasena, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC0392B), modifier = Modifier.padding(bottom = 16.dp))

                    OutlinedTextField(
                        value = passwordActual,
                        onValueChange = { passwordActual = it },
                        label = { Text(t.contrasenaActual) },
                        singleLine = true,
                        visualTransformation = if (mostrarPasswordActual) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { mostrarPasswordActual = !mostrarPasswordActual }) {
                                Icon(
                                    imageVector = if (mostrarPasswordActual) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = colorSubtexto
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordNueva,
                        onValueChange = { passwordNueva = it },
                        label = { Text(t.contrasenaNueva) },
                        singleLine = true,
                        visualTransformation = if (mostrarPasswordNueva) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { mostrarPasswordNueva = !mostrarPasswordNueva }) {
                                Icon(
                                    imageVector = if (mostrarPasswordNueva) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = colorSubtexto
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordConfirm,
                        onValueChange = { passwordConfirm = it },
                        label = { Text(t.confirmarContrasena) },
                        singleLine = true,
                        visualTransformation = if (mostrarPasswordConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { mostrarPasswordConfirm = !mostrarPasswordConfirm }) {
                                Icon(
                                    imageVector = if (mostrarPasswordConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = colorSubtexto
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    if (error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error, color = Color(0xFFC0392B), fontSize = 13.sp)
                    }
                    if (exito.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = exito, color = Color(0xFF27AE60), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            error = ""
                            exito = ""
                            if (passwordActual.isBlank() || passwordNueva.isBlank() || passwordConfirm.isBlank()) {
                                error = t.campsObligatoris
                                return@Button
                            }
                            if (passwordNueva == passwordActual) {
                                error = t.contrasenaMatixa
                                return@Button
                            }
                            if (passwordNueva != passwordConfirm) {
                                error = t.contrasenasCoincidenError
                                return@Button
                            }
                            scope.launch {
                                cargando = true
                                try {
                                    val token = TokenManager(context).token.first() ?: return@launch
                                    val api = RetrofitClient.getClient(token)
                                    val response = api.cambiarPassword(CambiarPasswordRequest(passwordActual, passwordNueva))
                                    if (response.isSuccessful) {
                                        exito = t.contrasenaExito
                                        passwordActual = ""
                                        passwordNueva = ""
                                        passwordConfirm = ""
                                    } else {
                                        val mensaje = try {
                                            val json = JSONObject(response.errorBody()?.string() ?: "")
                                            json.getString("mensaje")
                                        } catch (e: Exception) {
                                            t.errorCambiarContrasena
                                        }
                                        error = mensaje
                                    }
                                } catch (e: Exception) {
                                    error = t.errorConexion
                                } finally {
                                    cargando = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                        enabled = !cargando
                    ) {
                        if (cargando) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        else Text(t.cambiarContrasena, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}