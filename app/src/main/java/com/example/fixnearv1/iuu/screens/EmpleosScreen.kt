package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.modelo.EmpleoDemo
import com.example.fixnearv1.ui.components.FixNearMap
import com.example.fixnearv1.ui.components.TipoMapaFixNear

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleosScreen(
    onRegresar: () -> Unit,
    onVerDetalle: (EmpleoDemo) -> Unit
) {
    val empleos = listOf(
        EmpleoDemo("Café Central", "Barista", "$8,000 - $10,000", "8:00 AM - 4:00 PM", "2 km"),
        EmpleoDemo("FixNet", "Instalador de internet", "$10,000 - $13,000", "9:00 AM - 5:00 PM", "3 km"),
        EmpleoDemo("Casa Limpia", "Auxiliar de limpieza", "$7,000 - $9,000", "Medio tiempo", "1.5 km"),
        EmpleoDemo("ElectroMax", "Ayudante eléctrico", "$9,000 - $12,000", "Lunes a sábado", "4 km"),
        EmpleoDemo("Sushi Express", "Auxiliar de cocina", "$8,500", "Turno vespertino", "2.8 km"),
        EmpleoDemo("TecnoRepair", "Técnico de PC", "$9,500 - $12,000", "Tiempo completo", "3.5 km"),
        EmpleoDemo("AutoFix", "Ayudante mecánico", "$8,000 - $11,000", "Lunes a viernes", "4.2 km")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Empleos Cercanos")
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
                        .height(330.dp)
                ) {
                    FixNearMap(
                        tipoMapa = TipoMapaFixNear.EMPLEOS,
                        modifier = Modifier.fillMaxSize()
                    )

                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = {
                            Text("Buscar empleo cercano...")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 18.dp
                            )
                            .align(Alignment.TopCenter),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp),
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

                            Text("Vacantes a 5 km de ti")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Vacantes disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(empleos) { empleo ->
                EmpleoCardMapa(
                    empleo = empleo,
                    onVerVacante = {
                        onVerDetalle(empleo)
                    }
                )
            }
        }
    }
}

@Composable
fun EmpleoCardMapa(
    empleo: EmpleoDemo,
    onVerVacante: () -> Unit
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = empleo.empresa,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Vacante: ${empleo.puesto}")
            Text("Salario: ${empleo.salario}")
            Text("Horario: ${empleo.horario}")

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text("Distancia: ${empleo.distancia}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onVerVacante,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver vacante")
            }
        }
    }
}