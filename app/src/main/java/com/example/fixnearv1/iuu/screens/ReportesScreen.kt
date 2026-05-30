package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    onRegresar: () -> Unit = {}
) {
    var motivoSeleccionado by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }

    val motivos = listOf("spam", "fraude", "comportamiento_inapropiado", "contenido_falso", "otro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar usuario") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Flag,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Selecciona el motivo del reporte", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(12.dp))

            motivos.forEach { motivo ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = motivoSeleccionado == motivo,
                        onClick = { motivoSeleccionado = motivo }
                    )
                    Text(motivo.replace("_", " ").replaceFirstChar { it.uppercase() })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (enviado) {
                Text("Reporte enviado correctamente.", color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    if (motivoSeleccionado.isNotEmpty()) enviado = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = motivoSeleccionado.isNotEmpty()
            ) {
                Text("Enviar reporte")
            }
        }
    }
}
