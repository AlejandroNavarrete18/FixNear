package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTrabajadorScreen(
    onRegresar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del trabajador") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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

            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Carlos Ramírez", style = MaterialTheme.typography.headlineSmall)
            Text("Electricista profesional")

            Spacer(modifier = Modifier.height(8.dp))

            AssistChip(
                onClick = {},
                label = { Text("Trabajador verificado") },
                leadingIcon = {
                    Icon(Icons.Default.Verified, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            DatoTrabajadorCard("Calificación", "⭐ 4.9")
            DatoTrabajadorCard("Trabajos realizados", "120 servicios")
            DatoTrabajadorCard("Experiencia", "6 años")
            DatoTrabajadorCard("Zona de trabajo", "Culiacán, Sinaloa")
            DatoTrabajadorCard("Especialidad", "Instalaciones eléctricas y reparaciones urgentes")

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar este trabajador")
            }
        }
    }
}

@Composable
fun DatoTrabajadorCard(
    titulo: String,
    dato: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = dato,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

