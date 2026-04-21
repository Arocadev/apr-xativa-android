package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.viewmodel.AuthViewModel
import com.example.aprxtiva.viewmodel.SolicitudViewModel
import kotlinx.coroutines.launch

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
    val estadoSolicitud by solicitudViewModel.estadoSolicitud.collectAsState()

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
                        text = "La teua sol·licitud d'accés està sent revisada per l'administrador. Rebràs accés quan siga aprovada.",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                "RECHAZADA" -> {
                    Text(
                        text = "❌",
                        fontSize = 64.sp
                    )
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