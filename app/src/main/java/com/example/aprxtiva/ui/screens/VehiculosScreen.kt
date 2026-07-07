package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
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
import com.example.aprxtiva.entities.Vehiculo
import com.example.aprxtiva.ui.theme.TemaManager
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
    val errorMensaje by viewModel.errorMensaje.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var matricula by remember { mutableStateOf("") }
    var tipoAcred by remember { mutableStateOf("LIBRE") }
    var expandedTipo by remember { mutableStateOf(false) }
    var confirmandoId by remember { mutableStateOf<Long?>(null) }
    var mostrarInfoTipoAcred by remember { mutableStateOf(false) }
    val isRefreshing = estado is EstadoUI.Loading

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

    LaunchedEffect(Unit) { viewModel.cargarVehiculos() }

    if (mostrarInfoTipoAcred) {
        AlertDialog(
            onDismissRequest = { mostrarInfoTipoAcred = false },
            title = { Text(t.tipoAcred, fontWeight = FontWeight.Bold) },
            text = { Text(t.infoTipoAcred, lineHeight = 22.sp) },
            confirmButton = {
                TextButton(onClick = { mostrarInfoTipoAcred = false }) { Text(t.cancelar) }
            }
        )
    }

    if (errorMensaje != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarError() },
            title = { Text(t.error, fontWeight = FontWeight.Bold) },
            text = { Text(errorMensaje!!, lineHeight = 22.sp) },
            confirmButton = {
                TextButton(onClick = { viewModel.limpiarError() }) { Text(t.confirmar) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.vehiculos, fontWeight = FontWeight.Bold) },
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
            onRefresh = { viewModel.cargarVehiculos() },
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
                        Text(
                            text = t.addVehiculo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFC0392B),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (mostrarFormulario) {
                            OutlinedTextField(
                                value = matricula,
                                onValueChange = { matricula = it.uppercase() },
                                label = { Text(t.matricula) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                            Spacer(modifier = Modifier.height(10.dp))
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
                                        value = tipoAcred,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(t.tipoAcred) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = fieldColors
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedTipo,
                                        onDismissRequest = { expandedTipo = false }
                                    ) {
                                        listOf("LIBRE", "ACREDITADO").forEach { opcion ->
                                            DropdownMenuItem(
                                                text = { Text(opcion) },
                                                onClick = { tipoAcred = opcion; expandedTipo = false }
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { mostrarInfoTipoAcred = true }) {
                                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFFC0392B))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (matricula.isNotBlank()) {
                                        viewModel.addVehiculo(matricula, tipoAcred)
                                        matricula = ""
                                        mostrarFormulario = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                            ) {
                                Text(t.confirmar, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { mostrarFormulario = false; matricula = "" },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(t.cancelar, color = Color(0xFFC0392B))
                            }
                        } else {
                            Button(
                                onClick = { mostrarFormulario = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                            ) {
                                Text(t.addVehiculo, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                }

                when {
                    vehiculos.isEmpty() && estado !is EstadoUI.Loading -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colorCard)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🚗", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = t.sinVehiculos, color = colorSubtexto, fontSize = 15.sp)
                            }
                        }
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                    onReactivar = { viewModel.reactivarVehiculo(it) },
                                    t = t,
                                    colorCard = colorCard,
                                    colorTexto = colorTexto,
                                    colorSubtexto = colorSubtexto
                                )
                            }
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
    onReactivar: (Long) -> Unit,
    t: com.example.aprxtiva.utils.Textos,
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
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFFF0EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color(0xFFC0392B),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = vehiculo.matricula, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorTexto)
                    Text(text = vehiculo.tipoAcred, fontSize = 13.sp, color = colorSubtexto)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (vehiculo.activo) t.activo else t.inactivo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (vehiculo.activo) Color(0xFF27AE60) else Color(0xFFC0392B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (confirmandoId != vehiculo.id) {
                        if (vehiculo.activo) {
                            OutlinedButton(
                                onClick = { onConfirmarEliminar(vehiculo.id) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(t.desactivar, fontSize = 12.sp, color = Color(0xFFC0392B))
                            }
                        } else {
                            Button(
                                onClick = { onReactivar(vehiculo.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(t.reactivar, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (confirmandoId == vehiculo.id) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onEliminar(vehiculo.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text(t.confirmar, fontSize = 13.sp) }
                    OutlinedButton(
                        onClick = onCancelar,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text(t.cancelar, fontSize = 13.sp) }
                }
            }
        }
    }
}