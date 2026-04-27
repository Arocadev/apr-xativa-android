package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.entities.DerechoAcceso
import com.example.aprxtiva.entities.Vehiculo
import com.example.aprxtiva.utils.IdiomaManager
import com.example.aprxtiva.utils.Textos
import com.example.aprxtiva.viewmodel.DerechoViewModel
import com.example.aprxtiva.viewmodel.EstadoUI
import com.example.aprxtiva.viewmodel.VehiculoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DerechosScreen(
    onVolver: () -> Unit,
    viewModel: DerechoViewModel = viewModel(),
    vehiculoViewModel: VehiculoViewModel = viewModel()
) {
    val t = IdiomaManager.textos
    val derechos by viewModel.derechos.collectAsState()
    val estado by viewModel.estado.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var tipoDerecho by remember { mutableStateOf("PERMANENTE") }
    var expandedTipo by remember { mutableStateOf(false) }
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }
    var expandedVehiculo by remember { mutableStateOf(false) }
    var matriculaInvitado by remember { mutableStateOf("") }
    var fechaAcceso by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val hoy = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val mesSiguiente = Calendar.getInstance().apply {
                    add(Calendar.MONTH, 1)
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.timeInMillis
                return utcTimeMillis >= hoy && utcTimeMillis <= mesSiguiente
            }
        }
    )

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        fechaAcceso = sdf.format(Date(millis))
                    }
                    mostrarDatePicker = false
                }) { Text(t.confirmar) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text(t.cancelar) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarDerechos()
        vehiculoViewModel.cargarVehiculos()
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
                .verticalScroll(rememberScrollState())
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
                                            vehiculoSeleccionado = null
                                            matriculaInvitado = ""
                                            fechaAcceso = ""
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (tipoDerecho == "PERMANENTE") {
                            ExposedDropdownMenuBox(
                                expanded = expandedVehiculo,
                                onExpandedChange = { expandedVehiculo = !expandedVehiculo }
                            ) {
                                OutlinedTextField(
                                    value = vehiculoSeleccionado?.matricula ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(t.matricula) },
                                    placeholder = { Text("Selecciona un vehicle") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehiculo) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedVehiculo,
                                    onDismissRequest = { expandedVehiculo = false }
                                ) {
                                    if (vehiculos.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text(t.sinVehiculos) },
                                            onClick = { expandedVehiculo = false }
                                        )
                                    } else {
                                        vehiculos.forEach { vehiculo ->
                                            DropdownMenuItem(
                                                text = { Text("${vehiculo.matricula} — ${vehiculo.tipoAcred}") },
                                                onClick = {
                                                    vehiculoSeleccionado = vehiculo
                                                    expandedVehiculo = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    vehiculoSeleccionado?.let {
                                        viewModel.crearDerechoPermanente(it.id)
                                        mostrarFormulario = false
                                        vehiculoSeleccionado = null
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = vehiculoSeleccionado != null
                            ) {
                                Text(t.confirmar)
                            }

                        } else {
                            OutlinedTextField(
                                value = matriculaInvitado,
                                onValueChange = { matriculaInvitado = it.uppercase() },
                                label = { Text(t.matricula) },
                                placeholder = { Text("1234ABC") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = if (fechaAcceso.isEmpty()) "" else {
                                    val parts = fechaAcceso.split("-")
                                    if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else fechaAcceso
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(t.fecha) },
                                placeholder = { Text("dd/MM/yyyy") },
                                trailingIcon = {
                                    IconButton(onClick = { mostrarDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = t.fecha)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (matriculaInvitado.isNotBlank() && fechaAcceso.isNotBlank()) {
                                        viewModel.crearDerechoPuntualInvitado(matriculaInvitado, fechaAcceso)
                                        mostrarFormulario = false
                                        matriculaInvitado = ""
                                        fechaAcceso = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = matriculaInvitado.isNotBlank() && fechaAcceso.isNotBlank()
                            ) {
                                Text(t.confirmar)
                            }
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        derechos.forEach { derecho ->
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
                    text = derecho.matricula.ifEmpty { derecho.matriculaInvitado ?: "" },
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
                text = "${t.fechaInicio}: ${formatearFecha(derecho.fechaInicio)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (derecho.fechaFin.isNotEmpty() && derecho.fechaFin != derecho.fechaInicio) {
                Text(
                    text = "${t.fechaFin}: ${formatearFecha(derecho.fechaFin)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatearFecha(fecha: String): String {
    val parts = fecha.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else fecha
}