package com.example.fixnearv1.iuu.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class ServicioDemo(
    val nombre: String,
    val trabajador: String,
    val precio: String,
    val tiempo: String,
    val categoria: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    onRegresar: () -> Unit,
    onVerPerfilTrabajador: () -> Unit
) {
    val context = LocalContext.current

    var servicioSeleccionado by remember {
        mutableStateOf<ServicioDemo?>(null)
    }

    val servicios = listOf(
        ServicioDemo("Reparación eléctrica", "Carlos Ramírez", "$250 - $400", "15 min", "Electricidad"),
        ServicioDemo("Plomería urgente", "José López", "$300 - $500", "20 min", "Plomería"),
        ServicioDemo("Instalación de minisplit", "Miguel Torres", "$600 - $900", "35 min", "Clima"),
        ServicioDemo("Cerrajería", "Andrés Soto", "$180 - $350", "10 min", "Seguridad"),
        ServicioDemo("Reparación de PC", "Daniel Ruiz", "$250 - $600", "18 min", "Tecnología"),
        ServicioDemo("Pintura de casa", "Marco Vega", "$800 - $1500", "40 min", "Pintura"),
        ServicioDemo("Limpieza del hogar", "Ana López", "$300 - $700", "25 min", "Limpieza"),
        ServicioDemo("Carpintería", "Luis Herrera", "$200 - $700", "22 min", "Carpintería"),
        ServicioDemo("Mecánico a domicilio", "Raúl Medina", "$400 - $900", "30 min", "Mecánica")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Servicios Cercanos")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onRegresar
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
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
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFE7E2D8))
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = {
                            Text("Buscar servicio cercano...")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    MarcadorMapa(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF1565C0)
                    )

                    MarcadorMapa(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 55.dp),
                        color = Color(0xFF43A047)
                    )

                    MarcadorMapa(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 60.dp),
                        color = Color(0xFF43A047)
                    )

                    MarcadorMapa(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 60.dp),
                        color = Color(0xFF1565C0)
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White,
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text("Servicios a 5 km de ti")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Trabajadores disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(servicios) { servicio ->

                ServicioCardMapa(
                    servicio = servicio,
                    onVerPerfil = onVerPerfilTrabajador,
                    onMapa = {
                        val uri = Uri.parse(
                            "geo:24.8091,-107.3940?q=24.8091,-107.3940(${servicio.trabajador})"
                        )

                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            uri
                        )

                        intent.setPackage(
                            "com.google.android.apps.maps"
                        )

                        context.startActivity(intent)
                    },
                    onSolicitar = {
                        servicioSeleccionado = servicio
                    }
                )
            }
        }
    }

    if (servicioSeleccionado != null) {

        AlertDialog(
            onDismissRequest = {
                servicioSeleccionado = null
            },
            title = {
                Text("Servicio solicitado")
            },
            text = {
                Text(
                    "Tu solicitud de ${servicioSeleccionado!!.nombre} fue enviada a ${servicioSeleccionado!!.trabajador}."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        servicioSeleccionado = null
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun ServicioCardMapa(
    servicio: ServicioDemo,
    onVerPerfil: () -> Unit,
    onMapa: () -> Unit,
    onSolicitar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 7.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text("Trabajador: ${servicio.trabajador}")
            Text("Categoría: ${servicio.categoria}")
            Text("Precio estimado: ${servicio.precio}")
            Text("Llegada aproximada: ${servicio.tiempo}")

            Spacer(modifier = Modifier.height(10.dp))

            AssistChip(
                onClick = {},
                label = {
                    Text("Trabajador verificado")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVerPerfil,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver perfil del trabajador")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onMapa,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver mapa")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onSolicitar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Solicitar")
                }
            }
        }
    }
}

@Composable
fun MarcadorMapa(
    modifier: Modifier = Modifier,
    color: Color
) {
    Surface(
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        color = color,
        shadowElevation = 8.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}