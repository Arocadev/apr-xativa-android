package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.entities.DerechoAcceso
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.Textos
import com.example.aprxtiva.viewmodel.DerechoViewModel
import com.example.aprxtiva.viewmodel.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DerechosScreen(
    onVolver: () -> Unit,
    viewModel: DerechoViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val derechos by viewModel.derechos.collectAsState()
    val estado by viewModel.estado.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var tipoDerecho by remember { mutableStateOf("PERMANENTE") }
    var expandedTipo by remember { mutableStateOf(false) }
    var vehiculoId by remember { mutableStateOf("") }
    var tipoAcred by remember { mutableStateOf("LIBRE") }
    var expandedAcred by remember { mutableStateOf(false) }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var fechaAcceso by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarDerechos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.derechos, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            Button(
                onClick = { mostrarFormulario = !mostrarFormulario },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mostrarFormulario) t.cancelar else "Nou dret d'accés")
            }

            if (mostrarFormulario) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        ExposedDropdownMenuBox(
                            expanded = expandedTipo,
                            onExpandedChange = { expandedTipo = !expandedTipo }
                        ) {
                            OutlinedTextField(
                                value = tipoDerecho,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(t.tipoDerecho) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTipo,
                                onDismissRequest = { expandedTipo = false }
                            ) {
                                listOf("PERMANENTE", "PUNTUAL").forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            tipoDerecho = opcion
                                            expandedTipo = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = vehiculoId,
                            onValueChange = { vehiculoId = it },
                            label = { Text("ID Vehículo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedAcred,
                            onExpandedChange = { expandedAcred = !expandedAcred }
                        ) {
                            OutlinedTextField(
                                value = tipoAcred,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(t.tipoAcred) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAcred) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedAcred,
                                onDismissRequest = { expandedAcred = false }
                            ) {
                                listOf("LIBRE", "ACREDITADO").forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            tipoAcred = opcion
                                            expandedAcred = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (tipoDerecho == "PERMANENTE") {
                            OutlinedTextField(
                                value = fechaInicio,
                                onValueChange = { fechaInicio = it },
                                label = { Text("${t.fechaInicio} (yyyy-MM-dd)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = fechaFin,
                                onValueChange = { fechaFin = it },
                                label = { Text("${t.fechaFin} (yyyy-MM-dd)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            OutlinedTextField(
                                value = fechaAcceso,
                                onValueChange = { fechaAcceso = it },
                                label = { Text("${t.fecha} (yyyy-MM-dd)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val id = vehiculoId.toLongOrNull() ?: return@Button
                                if (tipoDerecho == "PERMANENTE") {
                                    viewModel.crearDerechoPermanente(id, tipoAcred, fechaInicio, fechaFin)
                                } else {
                                    viewModel.crearDerechoPuntual(id, tipoAcred, fechaAcceso)
                                }
                                mostrarFormulario = false
                                vehiculoId = ""
                                fechaInicio = ""
                                fechaFin = ""
                                fechaAcceso = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(t.confirmar)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                estado is EstadoUI.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                derechos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(t.sinDerechos, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(derechos) { derecho ->
                            DerechoCard(derecho = derecho, t = t)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DerechoCard(derecho: DerechoAcceso, t: Textos) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = derecho.matricula,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (derecho.activo) t.activo else t.inactivo,
                    fontSize = 12.sp,
                    color = if (derecho.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = derecho.tipoDerecho,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${t.fechaInicio}: ${derecho.fechaInicio}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (derecho.fechaFin.isNotEmpty()) {
                Text(
                    text = "${t.fechaFin}: ${derecho.fechaFin}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}