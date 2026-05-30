package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.modelo.EmpleoDemo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVacanteScreen(
    empleo: EmpleoDemo, // Recibe el empleo seleccionado
    onRegresar: () -> Unit // Función para volver atrás
) {
    // Controla si se muestra la alerta o no
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Vacante") },
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
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(text = empleo.empresa, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Puesto: ${empleo.puesto}", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Salario: ${empleo.salario}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Horario: ${empleo.horario}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Distancia a ti: ${empleo.distancia}", style = MaterialTheme.typography.bodyLarge)

            // Empuja el botón hasta abajo
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { mostrarDialogo = true }, // Al hacer clic, mostramos la alerta
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Work, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aplicar")
            }
        }
    }

    // La alerta que antes estaba en la otra pantalla ahora está aquí
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Postulación enviada") },
            text = { Text("Aplicaste a ${empleo.puesto} en ${empleo.empresa}. La empresa podrá revisar tu perfil.") },
            confirmButton = {
                Button(onClick = { mostrarDialogo = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
