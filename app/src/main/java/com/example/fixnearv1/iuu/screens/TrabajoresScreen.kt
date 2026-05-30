package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TrabajadorDemo(
    val nombre: String,
    val especialidad: String,
    val calificacion: String,
    val trabajos: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrabajadoresScreen(
    onRegresar: () -> Unit
) {
    var trabajadorSeleccionado by remember { mutableStateOf<TrabajadorDemo?>(null) }

    val trabajadores = listOf(
        TrabajadorDemo("Carlos Ramírez", "Electricista", "4.9", "120 trabajos"),
        TrabajadorDemo("José López", "Plomero", "4.8", "95 trabajos"),
        TrabajadorDemo("Miguel Torres", "Técnico de minisplit", "4.7", "80 trabajos"),
        TrabajadorDemo("Ana López", "Limpieza del hogar", "5.0", "60 trabajos"),
        TrabajadorDemo("Daniel Ruiz", "Técnico de PC", "4.9", "140 trabajos")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trabajadores Verificados") },
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
            items(trabajadores) { trabajador ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(62.dp),
                            shape = CircleShape,
                            color = Color(0xFF334155)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {

                            Text(
                                trabajador.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(trabajador.especialidad)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B)
                                )
                                Text("${trabajador.calificacion} • ${trabajador.trabajos}")
                            }

                            AssistChip(
                                onClick = {},
                                label = { Text("Verificado") },
                                leadingIcon = {
                                    Icon(Icons.Default.Verified, contentDescription = null)
                                }
                            )
                        }

                        Button(
                            onClick = { trabajadorSeleccionado = trabajador }
                        ) {
                            Text("Ver")
                        }
                    }
                }
            }
        }
    }

    if (trabajadorSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { trabajadorSeleccionado = null },
            title = { Text(trabajadorSeleccionado!!.nombre) },
            text = {
                Text(
                    "Especialidad: ${trabajadorSeleccionado!!.especialidad}\nCalificación: ${trabajadorSeleccionado!!.calificacion}\nTrabajos realizados: ${trabajadorSeleccionado!!.trabajos}"
                )
            },
            confirmButton = {
                Button(onClick = { trabajadorSeleccionado = null }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
