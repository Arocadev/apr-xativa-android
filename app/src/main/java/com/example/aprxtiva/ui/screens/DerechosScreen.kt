package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aprxtiva.entities.DerechoAcceso
import com.example.aprxtiva.entities.Vehiculo
import com.example.aprxtiva.ui.theme.TemaManager
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
    val errorMensaje by viewModel.errorMensaje.collectAsState()
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val isRefreshing = estado is EstadoUI.Loading

    var mostrarFormulario by remember { mutableStateOf(false) }
    var tipoDerecho by remember { mutableStateOf("PERMANENTE") }
    var expandedTipo by remember { mutableStateOf(false) }
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }
    var expandedVehiculo by remember { mutableStateOf(false) }
    var matriculaInvitado by remember { mutableStateOf("") }
    var fechaAcceso by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarInfoTipoDerecho by remember { mutableStateOf(false) }
    var mostrarInfoFechaPuntual by remember { mutableStateOf(false) }

    val oscuro = TemaManager.oscuro
    val colorFondo = if (oscuro) Color(0xFF1C1C1C) else Color(0xFFF8F7F5)
    val colorTexto = if (oscuro) Color.White else Color(0xFF2C2C2C)
    val colorSubtexto = if (oscuro) Color(0xFFAAAAAA) else Color.Gray
    val colorCard = if (oscuro) Color(0xFF2C2C2C) else Color.White

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFC0392B),
        unfocusedBorderColor = colorSubtexto,
        focusedLabelColor = Color(0xFFC0392B),
        unfocusedLabelColor = colorSubtexto,
        cursorColor = Color(0xFFC0392B),
        focusedTextColor = colorTexto,
        unfocusedTextColor = colorTexto
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val hoy = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val mesSiguiente = Calendar.getInstance().apply {
                    add(Calendar.MONTH, 1)
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59)
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
                        fechaAcceso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                    }
                    mostrarDatePicker = false
                }) { Text(t.confirmar) }
            },
            dismissButton = { TextButton(onClick = { mostrarDatePicker = false }) { Text(t.cancelar) } }
        ) { DatePicker(state = datePickerState) }
    }

    if (mostrarInfoTipoDerecho) {
        AlertDialog(
            onDismissRequest = { mostrarInfoTipoDerecho = false },
            title = { Text(t.tipoDerecho, fontWeight = FontWeight.Bold) },
            text = { Text(t.infoTipoDerecho, lineHeight = 22.sp) },
            confirmButton = { TextButton(onClick = { mostrarInfoTipoDerecho = false }) { Text(t.cancelar) } }
        )
    }

    if (mostrarInfoFechaPuntual) {
        AlertDialog(
            onDismissRequest = { mostrarInfoFechaPuntual = false },
            title = { Text(t.fecha, fontWeight = FontWeight.Bold) },
            text = { Text(t.infoDerechoPuntual, lineHeight = 22.sp) },
            confirmButton = { TextButton(onClick = { mostrarInfoFechaPuntual = false }) { Text(t.cancelar) } }
        )
    }

    if (errorMensaje != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarError() },
            title = { Text(t.error, fontWeight = FontWeight.Bold) },
            text = { Text(errorMensaje!!, lineHeight = 22.sp) },
            confirmButton = { TextButton(onClick = { viewModel.limpiarError() }) { Text(t.confirmar) } }
        )
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC0392B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.cargarDerechos()
                vehiculoViewModel.cargarVehiculos()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorFondo)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = t.nouDret, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFC0392B), modifier = Modifier.padding(bottom = 16.dp))

                        if (mostrarFormulario) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                ExposedDropdownMenuBox(expanded = expandedTipo, onExpandedChange = { expandedTipo = !expandedTipo }, modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(value = tipoDerecho, onValueChange = {}, readOnly = true, label = { Text(t.tipoDerecho) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                                    ExposedDropdownMenu(expanded = expandedTipo, onDismissRequest = { expandedTipo = false }) {
                                        listOf("PERMANENTE" to t.permanent, "PUNTUAL" to t.puntual).forEach { (valor, etiqueta) ->
                                            DropdownMenuItem(text = { Text(etiqueta) }, onClick = {
                                                tipoDerecho = valor; expandedTipo = false
                                                vehiculoSeleccionado = null; matriculaInvitado = ""; fechaAcceso = ""
                                            })
                                        }
                                    }
                                }
                                IconButton(onClick = { mostrarInfoTipoDerecho = true }) {
                                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFFC0392B))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (tipoDerecho == "PERMANENTE") {
                                ExposedDropdownMenuBox(expanded = expandedVehiculo, onExpandedChange = { expandedVehiculo = !expandedVehiculo }) {
                                    OutlinedTextField(value = vehiculoSeleccionado?.matricula ?: "", onValueChange = {}, readOnly = true, label = { Text(t.matricula) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehiculo) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                                    ExposedDropdownMenu(expanded = expandedVehiculo, onDismissRequest = { expandedVehiculo = false }) {
                                        if (vehiculos.isEmpty()) {
                                            DropdownMenuItem(text = { Text(t.sinVehiculos) }, onClick = { expandedVehiculo = false })
                                        } else {
                                            vehiculos.forEach { vehiculo ->
                                                DropdownMenuItem(text = { Text("${vehiculo.matricula} — ${vehiculo.tipoAcred}") }, onClick = { vehiculoSeleccionado = vehiculo; expandedVehiculo = false })
                                            }
                                        }
                                    }
                                }
                            } else {
                                OutlinedTextField(value = matriculaInvitado, onValueChange = { matriculaInvitado = it.uppercase() }, label = { Text(t.matricula) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = if (fechaAcceso.isEmpty()) "" else { val p = fechaAcceso.split("-"); if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else fechaAcceso },
                                        onValueChange = {}, readOnly = true, label = { Text(t.fecha) },
                                        trailingIcon = { IconButton(onClick = { mostrarDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = t.fecha) } },
                                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors
                                    )
                                    IconButton(onClick = { mostrarInfoFechaPuntual = true }) {
                                        Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFFC0392B))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (tipoDerecho == "PERMANENTE") {
                                        vehiculoSeleccionado?.let {
                                            viewModel.crearDerechoPermanente(it.id)
                                            mostrarFormulario = false
                                            vehiculoSeleccionado = null
                                        }
                                    } else {
                                        if (matriculaInvitado.isNotBlank() && fechaAcceso.isNotBlank()) {
                                            viewModel.crearDerechoPuntualInvitado(matriculaInvitado, fechaAcceso)
                                            mostrarFormulario = false
                                            matriculaInvitado = ""
                                            fechaAcceso = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                                enabled = if (tipoDerecho == "PERMANENTE") vehiculoSeleccionado != null else matriculaInvitado.isNotBlank() && fechaAcceso.isNotBlank()
                            ) {
                                Text(t.confirmar, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { mostrarFormulario = false; vehiculoSeleccionado = null; matriculaInvitado = ""; fechaAcceso = "" },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text(t.cancelar, color = Color(0xFFC0392B)) }

                        } else {
                            Button(
                                onClick = { mostrarFormulario = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                            ) { Text(t.nouDret, fontWeight = FontWeight.SemiBold, color = Color.White) }
                        }
                    }
                }

                when {
                    derechos.isEmpty() && estado !is EstadoUI.Loading -> {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colorCard)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔑", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = t.sinDerechos, color = colorSubtexto, fontSize = 15.sp)
                            }
                        }
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            derechos.forEach { derecho ->
                                DerechoCard(derecho = derecho, t = t, colorCard = colorCard, colorTexto = colorTexto, colorSubtexto = colorSubtexto)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DerechoCard(
    derecho: DerechoAcceso,
    t: Textos,
    colorCard: Color = Color.White,
    colorTexto: Color = Color(0xFF2C2C2C),
    colorSubtexto: Color = Color.Gray
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFF0EE)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFC0392B), modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            derecho.matricula.isNotEmpty() -> derecho.matricula
                            !derecho.matriculaInvitado.isNullOrEmpty() -> "${derecho.matriculaInvitado} (${t.convidat})"
                            else -> "-"
                        },
                        fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorTexto
                    )
                    Text(
                        text = if (derecho.activo) t.activo else t.inactivo,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium,
                        color = if (derecho.activo) Color(0xFF27AE60) else Color(0xFFC0392B)
                    )
                }
                Text(text = derecho.tipoDerecho, fontSize = 13.sp, color = Color(0xFFC0392B), fontWeight = FontWeight.Medium)
                Text(text = "${t.fechaInicio}: ${formatearFecha(derecho.fechaInicio)}", fontSize = 12.sp, color = colorSubtexto)
                if (derecho.fechaFin.isNotEmpty() && derecho.fechaFin != derecho.fechaInicio) {
                    Text(text = "${t.fechaFin}: ${formatearFecha(derecho.fechaFin)}", fontSize = 12.sp, color = colorSubtexto)
                }
            }
        }
    }
}

fun formatearFecha(fecha: String): String {
    val parts = fecha.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else fecha
}