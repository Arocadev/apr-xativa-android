package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprxtiva.ui.theme.TemaManager
import com.example.aprxtiva.utils.IdiomaManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiaScreen(onVolver: () -> Unit) {
    val t = IdiomaManager.textos

    val oscuro = TemaManager.oscuro
    val colorFondo = if (oscuro) Color(0xFF1C1C1C) else Color(0xFFF8F7F5)
    val colorTexto = if (oscuro) Color.White else Color(0xFF2C2C2C)
    val colorSubtexto = if (oscuro) Color(0xFFAAAAAA) else Color.DarkGray
    val colorCard = if (oscuro) Color(0xFF2C2C2C) else Color.White
    val colorContactCard = if (oscuro) Color(0xFF2C2420) else Color(0xFFFFF3E0)

    val pasos = listOf(
        Triple("📝", t.guiaPaso1Titulo, t.guiaPaso1Desc),
        Triple("📄", t.guiaPaso2Titulo, t.guiaPaso2Desc),
        Triple("⏳", t.guiaPaso3Titulo, t.guiaPaso3Desc),
        Triple("✅", t.guiaPaso4Titulo, t.guiaPaso4Desc),
        Triple("🚗", t.guiaPaso5Titulo, t.guiaPaso5Desc),
        Triple("🔑", t.guiaPaso6Titulo, t.guiaPaso6Desc),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.guia, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = t.guiaTitulo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC0392B),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            pasos.forEach { (emoji, titulo, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color(0xFFC0392B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = titulo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFC0392B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 14.sp,
                                color = colorSubtexto,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            // Card contacto
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorContactCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❓",
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = t.necesitasAyuda,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        color = colorTexto,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ajuntament de Xàtiva",
                        fontSize = 14.sp,
                        color = colorSubtexto,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📞 962 279 000",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC0392B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "✉️ info@xativa.es",
                        fontSize = 14.sp,
                        color = colorSubtexto,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}