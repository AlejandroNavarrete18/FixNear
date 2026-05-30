package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class SolicitudDemo(
    val cliente: String,
    val servicio: String,
    val distancia: String,
    val pago: String,
    val tiempo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrabajadorHomeScreen(
    onRegresar: () -> Unit
) {
    var disponible by remember { mutableStateOf(true) }
    var solicitudSeleccionada by remember { mutableStateOf<SolicitudDemo?>(null) }

    val solicitudes = listOf(
        SolicitudDemo("María López", "Plomería urgente", "1.8 km", "$450", "12 min"),
        SolicitudDemo("Juan Pérez", "Reparación eléctrica", "2.4 km", "$350", "18 min"),
        SolicitudDemo("Ana Torres", "Instalación de lámpara", "3.1 km", "$280", "22 min"),
        SolicitudDemo("Carlos Medina", "Cambio de contacto", "1.2 km", "$220", "10 min")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel del trabajador") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF4F6F8)),
            contentPadding = PaddingValues(16.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF334155)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(70.dp),
                            shape = CircleShape,
                            color = Color(0xFF0F172A)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(58.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Carlos Ramírez", color = Color.White)
                            Text("Electricista verificado", color = Color.LightGray)
                            Text("⭐ 4.9 • 120 trabajos", color = Color.LightGray)
                        }

                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Estado de disponibilidad")
                            Text(
                                if (disponible) "Disponible para recibir trabajos"
                                else "Fuera de servicio",
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = disponible,
                            onCheckedChange = { disponible = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GananciaCard(
                        titulo = "Hoy",
                        valor = "$850",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    GananciaCard(
                        titulo = "Semana",
                        valor = "$4,200",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Solicitudes cercanas",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            items(solicitudes) { solicitud ->
                SolicitudCard(
                    solicitud = solicitud,
                    onAceptar = {
                        solicitudSeleccionada = solicitud
                    }
                )
            }
        }
    }

    if (solicitudSeleccionada != null) {
        AlertDialog(
            onDismissRequest = { solicitudSeleccionada = null },
            title = { Text("Solicitud aceptada") },
            text = {
                Text(
                    "Aceptaste el servicio de ${solicitudSeleccionada!!.servicio} para ${solicitudSeleccionada!!.cliente}. Tiempo estimado de llegada: ${solicitudSeleccionada!!.tiempo}."
                )
            },
            confirmButton = {
                Button(onClick = { solicitudSeleccionada = null }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun GananciaCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(titulo, color = Color.Gray)
            Text(
                valor,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun SolicitudCard(
    solicitud: SolicitudDemo,
    onAceptar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                solicitud.servicio,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Cliente: ${solicitud.cliente}")
            Text("Distancia: ${solicitud.distancia}")
            Text("Pago estimado: ${solicitud.pago}")
            Text("Llegada estimada: ${solicitud.tiempo}")

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mapa")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onAceptar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}
