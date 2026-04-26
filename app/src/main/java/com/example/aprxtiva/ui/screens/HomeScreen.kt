package com.example.aprxtiva.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.repository.DocumentoRepository
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.TokenManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToVehiculos: () -> Unit,
    onNavigateToDerechos: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToAjustes: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val email by viewModel.email.collectAsState(initial = "")
    val activo by viewModel.activo.collectAsState(initial = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var uriSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var nombreArchivo by remember { mutableStateOf("") }
    var documentoSubido by remember { mutableStateOf(false) }
    var subiendo by remember { mutableStateOf(false) }
    var errorEnvio by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriSeleccionada = it
            nombreArchivo = uri.lastPathSegment?.replace(":", "_") ?: "documento"
            errorEnvio = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("APR Xàtiva", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToAjustes) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustos")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.logout()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!activo) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚠️ Compte pendent d'aprovació",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF856404)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Has d'enviar la documentació per desbloquejar l'accés.",
                            fontSize = 13.sp,
                            color = Color(0xFF856404)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (documentoSubido) {
                            Text(
                                text = "✅ Document enviat. L'administrador el revisarà prompte.",
                                fontSize = 13.sp,
                                color = Color(0xFF856404)
                            )
                        } else {
                            OutlinedButton(
                                onClick = { launcher.launch("*/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (nombreArchivo.isEmpty()) "Seleccionar document" else "📄 $nombreArchivo",
                                    fontSize = 13.sp
                                )
                            }

                            if (nombreArchivo.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        uriSeleccionada?.let { uri ->
                                            scope.launch {
                                                subiendo = true
                                                errorEnvio = ""
                                                try {
                                                    val token = TokenManager(context).token.first() ?: return@launch
                                                    val inputStream = context.contentResolver.openInputStream(uri)
                                                    val bytes = inputStream?.readBytes() ?: return@launch
                                                    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
                                                    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                                                    val part = MultipartBody.Part.createFormData("archivo", nombreArchivo, requestBody)
                                                    val result = DocumentoRepository(token).subirDocumento(part)
                                                    if (result.isSuccess) {
                                                        documentoSubido = true
                                                    } else {
                                                        errorEnvio = "Error en enviar el document"
                                                    }
                                                } catch (e: Exception) {
                                                    errorEnvio = t.errorConexion
                                                } finally {
                                                    subiendo = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = !subiendo,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF856404))
                                ) {
                                    if (subiendo) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                    } else {
                                        Text("Enviar document", color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }

                            if (errorEnvio.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = errorEnvio, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${t.bienvenido}!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            email?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HomeCard(
                        titulo = t.misVehiculos,
                        icono = Icons.Default.DirectionsCar,
                        onClick = onNavigateToVehiculos,
                        bloqueado = !activo
                    )
                }
                item {
                    HomeCard(
                        titulo = t.misDerechos,
                        icono = Icons.Default.Lock,
                        onClick = onNavigateToDerechos,
                        bloqueado = !activo
                    )
                }
                item {
                    HomeCard(
                        titulo = t.miPerfil,
                        icono = Icons.Default.Person,
                        onClick = onNavigateToPerfil,
                        bloqueado = false
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    bloqueado: Boolean = false
) {
    Card(
        onClick = { if (!bloqueado) onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (bloqueado) 0.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bloqueado) Color(0xFFE0E0E0) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                modifier = Modifier.size(36.dp),
                tint = if (bloqueado) Color.Gray else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (bloqueado) Color.Gray else MaterialTheme.colorScheme.onSurface
            )
            if (bloqueado) {
                Text(text = "🔒", fontSize = 12.sp)
            }
        }
    }
}