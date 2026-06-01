package com.example.fixnearv1.iuu.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.ui.components.FixNearMap
import com.example.fixnearv1.ui.components.TipoMapaFixNear
import com.example.fixnearv1.modelo.ui.theme.*
import androidx.compose.foundation.BorderStroke
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
        ServicioDemo("Reparación de PC", "Daniel Ruiz", "$250 - $600", "18 min", "Tecnología")
    )

    Scaffold(
        containerColor = FondoPrincipal,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Servicios Cercanos",
                        color = TextoPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FondoPrincipal
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoPrincipal),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                            .border(
                                width = 1.dp,
                                color = BordeSuave,
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(24.dp),
                            color = CardOscura
                        ) {
                            Box {
                                FixNearMap(
                                    tipoMapa = TipoMapaFixNear.SERVICIOS,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = {
                                Text(
                                    "Buscar servicio cercano...",
                                    color = TextoSuave
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MoradoClaro
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopCenter),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = FondoSecundario,
                                unfocusedContainerColor = FondoSecundario,
                                focusedBorderColor = MoradoPrincipal,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = TextoPrincipal,
                                unfocusedTextColor = TextoPrincipal,
                                cursorColor = MoradoPrincipal
                            )
                        )

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(50.dp),
                            color = CardOscura2,
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
                                    contentDescription = null,
                                    tint = MoradoClaro
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Servicios a 5 km de ti",
                                    color = TextoPrincipal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Trabajadores disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            items(servicios) { servicio ->
                ServicioCardMapaMejorada(
                    servicio = servicio,
                    onVerPerfil = onVerPerfilTrabajador,
                    onMapa = {
                        val uri = Uri.parse(
                            "geo:24.8091,-107.3940?q=24.8091,-107.3940(${servicio.trabajador})"
                        )
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")
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
            onDismissRequest = { servicioSeleccionado = null },
            containerColor = CardOscura,
            title = {
                Text(
                    "Servicio solicitado",
                    color = TextoPrincipal
                )
            },
            text = {
                Text(
                    "Tu solicitud de ${servicioSeleccionado!!.nombre} fue enviada a ${servicioSeleccionado!!.trabajador}.",
                    color = TextoSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = { servicioSeleccionado = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoradoPrincipal
                    )
                ) {
                    Text("Aceptar", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ServicioCardMapaMejorada(
    servicio: ServicioDemo,
    onVerPerfil: () -> Unit,
    onMapa: () -> Unit,
    onSolicitar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardOscura
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Trabajador: ${servicio.trabajador}", color = TextoSecundario)
            Text("Categoría: ${servicio.categoria}", color = TextoSecundario)
            Text("Precio estimado: ${servicio.precio}", color = TextoSecundario)
            Text("Llegada aproximada: ${servicio.tiempo}", color = TextoSecundario)

            Spacer(modifier = Modifier.height(12.dp))

            AssistChip(
                onClick = {},
                label = {
                    Text("Trabajador verificado", color = TextoPrincipal)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = MoradoClaro
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = CardOscura2,
                    labelColor = TextoPrincipal
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = BordeSuave
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onVerPerfil,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MoradoClaro
                )
            ) {
                Text("Ver perfil del trabajador")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onMapa,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MoradoClaro
                    )
                ) {
                    Text("Ver mapa")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onSolicitar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoradoPrincipal
                    )
                ) {
                    Text("Solicitar", color = Color.White)
                }
            }
        }
    }
}