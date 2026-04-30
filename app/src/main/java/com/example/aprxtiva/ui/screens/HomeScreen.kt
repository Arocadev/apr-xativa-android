package com.example.aprxtiva.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.repository.DocumentoRepository
import com.example.aprxtiva.ui.theme.TemaManager
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
    onNavigateToGuia: () -> Unit,
    onLogout: () -> Unit,
    esLandscape: Boolean = false,
    viewModel: AuthViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val email by viewModel.email.collectAsState(initial = "")
    val activo by viewModel.activo.collectAsState(initial = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val oscuro = TemaManager.oscuro
    val colorFondo = if (oscuro) Color(0xFF1C1C1C) else Color(0xFFF8F7F5)
    val colorTexto = if (oscuro) Color.White else Color(0xFF2C2C2C)
    val colorSubtexto = if (oscuro) Color(0xFFAAAAAA) else Color.Gray
    val colorCard = if (oscuro) Color(0xFF2C2C2C) else Color.White
    val colorIconBg = if (oscuro) Color(0xFF3C2020) else Color(0xFFFFF0EE)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header rojo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC0392B))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "APR Xàtiva",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row {
                        IconButton(onClick = onNavigateToAjustes) {
                            Icon(Icons.Default.Settings, contentDescription = t.ajustes, tint = Color.White)
                        }
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.logout()
                                onLogout()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = t.cerrarSesion, tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = t.bienvenido,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = email ?: "",
                fontSize = 15.sp,
                color = colorSubtexto,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Banner inactivo
            if (!activo) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
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
                                                    if (result.isSuccess) documentoSubido = true
                                                    else errorEnvio = "Error en enviar el document"
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

            // Tarjetas
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeCard(
                    titulo = t.misVehiculos,
                    subtitulo = t.subtituloVehiculos,
                    icono = Icons.Default.DirectionsCar,
                    onClick = onNavigateToVehiculos,
                    bloqueado = !activo,
                    colorCard = colorCard,
                    colorTexto = colorTexto,
                    colorIconBg = colorIconBg
                )
                HomeCard(
                    titulo = t.misDerechos,
                    subtitulo = t.subtituloDerechos,
                    icono = Icons.Default.Lock,
                    onClick = onNavigateToDerechos,
                    bloqueado = !activo,
                    colorCard = colorCard,
                    colorTexto = colorTexto,
                    colorIconBg = colorIconBg
                )
                HomeCard(
                    titulo = t.guia,
                    subtitulo = t.subtituloGuia,
                    icono = Icons.Default.MenuBook,
                    onClick = onNavigateToGuia,
                    bloqueado = false,
                    colorCard = colorCard,
                    colorTexto = colorTexto,
                    colorIconBg = colorIconBg
                )
            }

            Spacer(modifier = Modifier.height(88.dp))
        }

        // FAB perfil
        FloatingActionButton(
            onClick = onNavigateToPerfil,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFFC0392B),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Person, contentDescription = t.miPerfil)
        }
    }
}

@Composable
fun HomeCard(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    bloqueado: Boolean = false,
    colorCard: Color = Color.White,
    colorTexto: Color = Color(0xFF2C2C2C),
    colorIconBg: Color = Color(0xFFFFF0EE)
) {
    Card(
        onClick = { if (!bloqueado) onClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (bloqueado) 0.dp else 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bloqueado) Color(0xFFE0E0E0) else colorCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (bloqueado) Color(0xFFBDBDBD) else colorIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    modifier = Modifier.size(28.dp),
                    tint = if (bloqueado) Color.Gray else Color(0xFFC0392B)
                )
            }
            Column {
                Text(
                    text = if (bloqueado) "$titulo 🔒" else titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (bloqueado) Color.Gray else colorTexto
                )
                Text(
                    text = subtitulo,
                    fontSize = 13.sp,
                    color = if (bloqueado) Color.Gray else Color(0xFF888888)
                )
            }
        }
    }
}