package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.aprxtiva.entities.Vehiculo
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.viewmodel.EstadoUI
import com.example.aprxtiva.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculosScreen(
    onVolver: () -> Unit,
    viewModel: VehiculoViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val vehiculos by viewModel.vehiculos.collectAsState()
    val estado by viewModel.estado.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var matricula by remember { mutableStateOf("") }
    var tipoAcred by remember { mutableStateOf("LIBRE") }
    var expandedTipo by remember { mutableStateOf(false) }
    var confirmandoId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarVehiculos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.vehiculos, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Button(
                onClick = { mostrarFormulario = !mostrarFormulario },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mostrarFormulario) t.cancelar else t.addVehiculo)
            }

            if (mostrarFormulario) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = matricula,
                            onValueChange = { matricula = it.uppercase() },
                            label = { Text(t.matricula) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedTipo,
                            onExpandedChange = { expandedTipo = !expandedTipo }
                        ) {
                            OutlinedTextField(
                                value = tipoAcred,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(t.tipoAcred) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTipo,
                                onDismissRequest = { expandedTipo = false }
                            ) {
                                listOf("LIBRE", "ACREDITADO").forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            tipoAcred = opcion
                                            expandedTipo = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (matricula.isNotBlank()) {
                                    viewModel.addVehiculo(matricula, tipoAcred)
                                    matricula = ""
                                    mostrarFormulario = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(t.addVehiculo)
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
                vehiculos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(t.sinVehiculos, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        vehiculos.forEach { vehiculo ->
                            VehiculoCard(
                                vehiculo = vehiculo,
                                confirmandoId = confirmandoId,
                                onConfirmarEliminar = { confirmandoId = it },
                                onCancelar = { confirmandoId = null },
                                onEliminar = {
                                    viewModel.deleteVehiculo(it)
                                    confirmandoId = null
                                },
                                t = t
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehiculoCard(
    vehiculo: Vehiculo,
    confirmandoId: Long?,
    onConfirmarEliminar: (Long) -> Unit,
    onCancelar: () -> Unit,
    onEliminar: (Long) -> Unit,
    t: com.example.aprxtiva.utils.Textos
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = vehiculo.matricula, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = vehiculo.tipoAcred, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = if (vehiculo.activo) t.activo else t.inactivo,
                    fontSize = 12.sp,
                    color = if (vehiculo.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            if (confirmandoId == vehiculo.id) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onEliminar(vehiculo.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(t.confirmar)
                    }
                    OutlinedButton(onClick = onCancelar) {
                        Text(t.cancelar)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onConfirmarEliminar(vehiculo.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t.eliminar)
                }
            }
        }
    }
}