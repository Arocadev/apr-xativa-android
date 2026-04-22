package com.example.aprxtiva.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.repository.DocumentoRepository
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.TokenManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import com.example.aprxtiva.viewmodel.SolicitudViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsperaScreen(
    onLogout: () -> Unit,
    onAprobado: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    solicitudViewModel: SolicitudViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val estadoSolicitud by solicitudViewModel.estadoSolicitud.collectAsState()
    var documentoSubido by remember { mutableStateOf(false) }
    var subiendo by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                subiendo = true
                mensajeError = ""
                try {
                    val token = TokenManager(context).token.first() ?: return@launch
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes() ?: return@launch
                    val nombreArchivo = uri.lastPathSegment ?: "documento.pdf"
                    val requestBody = bytes.toRequestBody("application/pdf".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("archivo", nombreArchivo, requestBody)
                    val result = DocumentoRepository(token).subirDocumento(part)
                    if (result.isSuccess) {
                        documentoSubido = true
                    } else {
                        mensajeError = "Error al subir el documento"
                    }
                } catch (e: Exception) {
                    mensajeError = "Error de connexió"
                } finally {
                    subiendo = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        solicitudViewModel.getEstadoSolicitud()
    }

    LaunchedEffect(estadoSolicitud) {
        if (estadoSolicitud?.estado == "APROBADA") {
            onAprobado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("APR Xàtiva", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            authViewModel.logout()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = t.cerrarSesion)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (estadoSolicitud?.estado) {
                "PENDIENTE" -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Sol·licitud pendent",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "La teua sol·licitud està sent revisada per l'administrador.",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (!documentoSubido) {
                        Text(
                            text = "Adjunta un document que acredite que resideixes en la zona APR (empadronament, contracte de lloguer...)",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { launcher.launch("*/*") },
                            enabled = !subiendo,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (subiendo) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Adjuntar document")
                            }
                        }
                        if (mensajeError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = mensajeError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Document enviat correctament. L'administrador el revisarà prompte.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                "RECHAZADA" -> {
                    Text(text = "❌", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Sol·licitud rebutjada",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    estadoSolicitud?.observaciones?.let {
                        Text(
                            text = "Motiu: $it",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = "Contacta amb l'Ajuntament de Xàtiva per a més informació.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    CircularProgressIndicator()
                }
            }
        }
    }
}