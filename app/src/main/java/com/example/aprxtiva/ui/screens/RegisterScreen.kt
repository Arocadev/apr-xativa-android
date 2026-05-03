package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.ui.theme.TemaManager
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import com.example.aprxtiva.viewmodel.RegistroState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var dni by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("A.1") }
    var expandedTipo by remember { mutableStateOf(false) }
    var mostrarInfoTipo by remember { mutableStateOf(false) }
    val registroState by viewModel.registroState.collectAsState()
    val t = IdiomaManager.textos

    val snackbarHostState = remember { SnackbarHostState() }

    val oscuro = TemaManager.oscuro
    val colorFondo = if (oscuro) Color(0xFF1C1C1C) else Color(0xFFF8F7F5)
    val colorTexto = if (oscuro) Color.White else Color(0xFF2C2C2C)
    val colorSubtexto = if (oscuro) Color(0xFFAAAAAA) else Color.Gray

    val tipos = listOf("A.1", "A.2", "A.3", "B", "C", "D", "E", "F", "G", "H.1", "H.2")

    LaunchedEffect(Unit) {
        viewModel.resetRegistroState()
    }

    LaunchedEffect(registroState) {
        if (registroState is RegistroState.Success) {
            snackbarHostState.showSnackbar(
                message = t.usuarioCreadoExito,
                duration = SnackbarDuration.Short
            )
            onRegistroSuccess()
        }
    }

    if (mostrarInfoTipo) {
        AlertDialog(
            onDismissRequest = { mostrarInfoTipo = false },
            title = { Text(t.tipo, fontWeight = FontWeight.Bold) },
            text = { Text(t.infoTipoUsuario, lineHeight = 22.sp) },
            confirmButton = {
                TextButton(onClick = { mostrarInfoTipo = false }) {
                    Text(t.cancelar)
                }
            }
        )
    }

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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = Color(0xFF27AE60),
                    contentColor = Color.White,
                    snackbarData = data
                )
            }
        },
        containerColor = colorFondo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = t.registroTitulo,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC0392B)
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it.uppercase() },
                    label = { Text(t.dni) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(t.nombre) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text(t.apellidos) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(t.email) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(t.contrasena) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expandedTipo,
                        onExpandedChange = { expandedTipo = !expandedTipo },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = tipo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(t.tipo) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false }
                        ) {
                            tipos.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        tipo = opcion
                                        expandedTipo = false
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = { mostrarInfoTipo = true }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFFC0392B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (registroState is RegistroState.Error) {
                    Text(
                        text = (registroState as RegistroState.Error).message,
                        color = Color(0xFFC0392B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.registro(dni, nombre, apellidos, email, password, tipo) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                    enabled = registroState !is RegistroState.Loading
                ) {
                    if (registroState is RegistroState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text(t.registrarse, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        t.yaTienesCuenta,
                        color = Color(0xFFC0392B),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}