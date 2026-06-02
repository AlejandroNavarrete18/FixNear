package com.example.fixnearv1.iuu.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.modelo.EmpleoDemo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVacanteScreen(
    empleo: EmpleoDemo,
    onRegresar: () -> Unit
) {
    val context = LocalContext.current

    val fondoPrincipal = Color(0xFF0F172A)
    val fondoSecundario = Color(0xFF1E293B)
    val cardColor = Color(0xFF334155)
    val morado = Color(0xFF7C3AED)
    val moradoClaro = Color(0xFFB388FF)
    val textoPrincipal = Color.White
    val textoSecundario = Color.LightGray

    Scaffold(
        containerColor = fondoPrincipal,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de la vacante",
                        color = textoPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = textoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = fondoPrincipal
                )
            )
        },
        bottomBar = {
            Surface(
                color = fondoSecundario,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val consulta = Uri.encode(empleo.empresa)
                            val uri = Uri.parse("geo:0,0?q=$consulta")
                            val intent = Intent(Intent.ACTION_VIEW, uri)

                            runCatching {
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = moradoClaro
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Mapa")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            // Después puedes guardar la postulación en Supabase
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = morado
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Aplicar",
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            fondoPrincipal,
                            fondoSecundario
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(62.dp),
                            shape = CircleShape,
                            color = Color(0xFF1E293B)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = moradoClaro,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = empleo.empresa,
                                style = MaterialTheme.typography.titleLarge,
                                color = textoPrincipal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = empleo.puesto,
                                color = textoSecundario
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "Vacante disponible",
                                color = textoPrincipal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFF1E293B),
                            labelColor = textoPrincipal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoVacanteMiniCard(
                    titulo = "Salario",
                    dato = empleo.salario,
                    icono = Icons.Default.MonetizationOn,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                InfoVacanteMiniCard(
                    titulo = "Distancia",
                    dato = empleo.distancia,
                    icono = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            InfoVacanteCard(
                titulo = "Horario",
                dato = empleo.horario,
                icono = Icons.Default.Schedule
            )

            InfoVacanteCard(
                titulo = "Ubicación",
                dato = "Culiacán, Sinaloa. Cercano a tu zona.",
                icono = Icons.Default.LocationOn
            )

            InfoVacanteCard(
                titulo = "Tipo de empleo",
                dato = "Presencial · Jornada laboral definida",
                icono = Icons.Default.Work
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Descripción del puesto",
                style = MaterialTheme.typography.titleMedium,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Text(
                    text = "La empresa ${empleo.empresa} busca una persona responsable para cubrir el puesto de ${empleo.puesto}. Se requiere puntualidad, buena actitud, disposición para aprender y compromiso con las actividades del negocio.",
                    color = textoSecundario,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Requisitos",
                style = MaterialTheme.typography.titleMedium,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequisitoItem("Disponibilidad en el horario indicado")
            RequisitoItem("Responsabilidad y puntualidad")
            RequisitoItem("Buena atención al cliente")
            RequisitoItem("Experiencia básica relacionada con el puesto")
            RequisitoItem("Vivir cerca de la zona o facilidad de traslado")

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Beneficios",
                style = MaterialTheme.typography.titleMedium,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequisitoItem("Pago competitivo según el puesto")
            RequisitoItem("Ambiente laboral estable")
            RequisitoItem("Oportunidad de crecimiento")
            RequisitoItem("Trabajo cercano a tu ubicación")

            Spacer(modifier = Modifier.height(18.dp))

            InfoVacanteCard(
                titulo = "Contacto",
                dato = "El negocio recibirá tu postulación desde la app. También puedes solicitar más información al aplicar.",
                icono = Icons.Default.Call
            )

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun InfoVacanteMiniCard(
    titulo: String,
    dato: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFFB388FF)
            )

            Column {
                Text(
                    text = titulo,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = dato,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun InfoVacanteCard(
    titulo: String,
    dato: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFFB388FF)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = titulo,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dato,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun RequisitoItem(
    texto: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = texto,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}