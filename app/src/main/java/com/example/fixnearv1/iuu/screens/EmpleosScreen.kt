package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.fixnearv1.modelo.ui.theme.*

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
        EmpleoDemo("ElectroMax", "Ayudante eléctrico", "$9,000 - $12,000", "Lunes a sábado", "4 km")
    )

    Scaffold(
        containerColor = FondoPrincipal,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Empleos Cercanos",
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
                            FixNearMap(
                                tipoMapa = TipoMapaFixNear.EMPLEOS,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = {
                                Text(
                                    "Buscar empleo cercano...",
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
                                    "Vacantes a 5 km de ti",
                                    color = TextoPrincipal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Vacantes disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            items(empleos) { empleo ->
                EmpleoCardMapaMejorada(
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
fun EmpleoCardMapaMejorada(
    empleo: EmpleoDemo,
    onVerVacante: () -> Unit
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = null,
                    tint = MoradoClaro
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = empleo.empresa,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Vacante: ${empleo.puesto}", color = TextoSecundario)
            Text("Salario: ${empleo.salario}", color = TextoSecundario)
            Text("Horario: ${empleo.horario}", color = TextoSecundario)

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MoradoClaro
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Distancia: ${empleo.distancia}", color = TextoSecundario)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onVerVacante,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoPrincipal
                )
            ) {
                Text("Ver vacante", color = Color.White)
            }
        }
    }
}