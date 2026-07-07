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
import com.example.aprxtiva.api.RetrofitClient
import com.example.aprxtiva.repository.DocumentoRepository
import com.example.aprxtiva.ui.theme.TemaManager
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.TokenManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
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
    val activo by viewModel.activo.collectAsState(initial = false)
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
    var estadoSolicitud by remember { mutableStateOf<String?>(null) }
    var saliendo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriSeleccionada = it
            val cursor = context.contentResolver.query(it, null, null, null, null)
            nombreArchivo = cursor?.use { c ->
                val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                c.moveToFirst()
                c.getString(nameIndex)
            } ?: uri.lastPathSegment?.replace(":", "_") ?: "documento"
            errorEnvio = ""
        }
    }

    LaunchedEffect(activo, saliendo) {
        if (!activo && !saliendo) {
            while (!saliendo) {
                try {
                    val token = TokenManager(context).token.first()
                    if (token == null) break
                    val api = RetrofitClient.getClient(token)
                    val response = api.getEstadoSolicitud()
                    if (response.isSuccessful) {
                        val solicitud = response.body()
                        estadoSolicitud = solicitud?.estado ?: "DESACTIVADO"
                        if (solicitud?.estado == "APROBADA") {
                            TokenManager(context).guardarActivo(true)
                            break
                        }
                    } else {
                        estadoSolicitud = "DESACTIVADO"
                    }
                } catch (e: Exception) {
                    estadoSolicitud = "DESACTIVADO"
                }
                var elapsed = 0
                while (elapsed < 10_000 && !saliendo) {
                    delay(100)
                    elapsed += 100
                }
                if (saliendo) break
            }
        }
    }

    fun subirDocumento(uri: Uri, crearNuevaSolicitud: Boolean) {
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
                    if (crearNuevaSolicitud) {
                        val api = RetrofitClient.getClient(token)
                        api.crearSolicitud()
                    }
                    documentoSubido = true
                    estadoSolicitud = "PENDIENTE"
                } else {
                    errorEnvio = t.errorConexion
                }
            } catch (e: Exception) {
                errorEnvio = t.errorConexion
            } finally {
                subiendo = false
            }
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
                            saliendo = true
                            onLogout()
                            scope.launch { viewModel.logout(context) }
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

            if (!activo && !saliendo) {
                when (estadoSolicitud) {
                    null -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFC0392B))
                        }
                    }
                    "DESACTIVADO" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = t.compteDesactivat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFC0392B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = t.compteDesactivatDesc,
                                    fontSize = 13.sp,
                                    color = Color(0xFFC0392B)
                                )
                            }
                        }
                    }
                    "RECHAZADA" -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = t.solicitudRebutjada,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF856404)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = t.solicitudRebutjadaDesc,
                                    fontSize = 13.sp,
                                    color = Color(0xFF856404)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                BannerSubirDocumento(
                                    nombreArchivo = nombreArchivo,
                                    documentoSubido = documentoSubido,
                                    subiendo = subiendo,
                                    errorEnvio = errorEnvio,
                                    textoSeleccionar = t.seleccionarDocument,
                                    textoEnviar = t.enviarDocument,
                                    textoEnviado = t.documentEnviat,
                                    onSeleccionar = { launcher.launch("*/*") },
                                    onEnviar = {
                                        uriSeleccionada?.let { uri ->
                                            subirDocumento(uri, crearNuevaSolicitud = true)
                                        }
                                    },
                                    colorBoton = Color(0xFF856404)
                                )
                            }
                        }
                    }
                    else -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = t.comptePendent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF856404)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = t.comptePendentDesc,
                                    fontSize = 13.sp,
                                    color = Color(0xFF856404)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                BannerSubirDocumento(
                                    nombreArchivo = nombreArchivo,
                                    documentoSubido = documentoSubido,
                                    subiendo = subiendo,
                                    errorEnvio = errorEnvio,
                                    textoSeleccionar = t.seleccionarDocument,
                                    textoEnviar = t.enviarDocument,
                                    textoEnviado = t.documentEnviat,
                                    onSeleccionar = { launcher.launch("*/*") },
                                    onEnviar = {
                                        uriSeleccionada?.let { uri ->
                                            subirDocumento(uri, crearNuevaSolicitud = false)
                                        }
                                    },
                                    colorBoton = Color(0xFF856404)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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
fun BannerSubirDocumento(
    nombreArchivo: String,
    documentoSubido: Boolean,
    subiendo: Boolean,
    errorEnvio: String,
    textoSeleccionar: String,
    textoEnviar: String,
    textoEnviado: String,
    onSeleccionar: () -> Unit,
    onEnviar: () -> Unit,
    colorBoton: Color
) {
    if (documentoSubido) {
        Text(text = textoEnviado, fontSize = 13.sp, color = Color(0xFF856404))
    } else {
        OutlinedButton(
            onClick = onSeleccionar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (nombreArchivo.isEmpty()) textoSeleccionar else "📄 $nombreArchivo",
                fontSize = 13.sp
            )
        }
        if (nombreArchivo.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onEnviar,
                enabled = !subiendo,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton)
            ) {
                if (subiendo) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text(textoEnviar, color = Color.White, fontSize = 13.sp)
                }
            }
        }
        if (errorEnvio.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = errorEnvio, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
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