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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SupabaseApi
import com.example.fixnearv1.utils.Vacante
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVacanteScreen(
    vacante: Vacante,
    onRegresar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var aplicando by remember {
        mutableStateOf(false)
    }

    var tituloDialogo by remember {
        mutableStateOf("")
    }

    var mensajeDialogo by remember {
        mutableStateOf("")
    }

    fun aplicarVacanteReal() {
        val userId = SesionLocal.obtenerUserId(context)
        val token = SesionLocal.obtenerAccessToken(context)

        if (userId.isBlank() || token.isBlank()) {
            tituloDialogo = "Sesión no válida"
            mensajeDialogo =
                "Inicia sesión nuevamente para aplicar a la vacante."
            return
        }

        aplicando = true

        scope.launch {
            val perfilResultado = SupabaseApi.obtenerPerfil(
                userId = userId,
                accessToken = token
            )

            if (perfilResultado.isFailure) {
                aplicando = false
                tituloDialogo = "No se pudo aplicar"
                mensajeDialogo = perfilResultado.exceptionOrNull()?.message
                    ?: "No se pudo obtener tu perfil."
                return@launch
            }

            val perfil = perfilResultado.getOrThrow()

            val resultado = SupabaseApi.aplicarVacante(
                accessToken = token,
                vacante = vacante,
                perfil = perfil
            )

            aplicando = false

            resultado.onSuccess {
                tituloDialogo = "Postulación enviada"
                mensajeDialogo =
                    "Aplicaste correctamente a ${vacante.puesto} en ${vacante.empresa}."
            }

            resultado.onFailure { exception ->
                tituloDialogo = "No se pudo aplicar"
                mensajeDialogo = exception.message
                    ?: "Ocurrió un error al guardar la postulación."
            }
        }
    }

    val fondoPrincipal = Color(0xFF0F172A)
    val fondoSecundario = Color(0xFF1E293B)
    val cardColor = Color(0xFF334155)
    val morado = Color(0xFF7C3AED)
    val moradoClaro = Color(0xFFB388FF)
    val textoPrincipal = Color.White
    val textoSecundario = Color.LightGray

    val descripcionVacante = vacante.descripcion
        .takeIf { it.isNotBlank() }
        ?: "La empresa ${vacante.empresa} busca una persona responsable para cubrir el puesto de ${vacante.puesto}. Se requiere puntualidad, buena actitud, disposición para aprender y compromiso con las actividades del negocio."

    val requisitosVacante = vacante.requisitos
        .takeIf { it.isNotBlank() }
        ?: "Disponibilidad en el horario indicado, responsabilidad, puntualidad y buena atención al cliente."

    val beneficiosVacante = vacante.beneficios
        .takeIf { it.isNotBlank() }
        ?: "Pago competitivo, ambiente laboral estable y oportunidad de crecimiento."

    val direccionVacante = vacante.direccion
        .takeIf { it.isNotBlank() }
        ?: "Culiacán, Sinaloa"

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
                            val uri = if (
                                vacante.latitud != null &&
                                vacante.longitud != null
                            ) {
                                Uri.parse(
                                    "geo:${vacante.latitud},${vacante.longitud}" +
                                            "?q=${vacante.latitud},${vacante.longitud}" +
                                            "(${Uri.encode(vacante.empresa)})"
                                )
                            } else {
                                val consulta = Uri.encode(
                                    "${vacante.empresa} $direccionVacante"
                                )
                                Uri.parse("geo:0,0?q=$consulta")
                            }

                            val intent = Intent(Intent.ACTION_VIEW, uri)

                            runCatching {
                                context.startActivity(intent)
                            }.onFailure {
                                tituloDialogo = "No se pudo abrir el mapa"
                                mensajeDialogo =
                                    "No hay una aplicación de mapas disponible."
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
                            aplicarVacanteReal()
                        },
                        enabled = !aplicando,
                        modifier = Modifier
                            .weight(1.4f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = morado,
                            disabledContainerColor = Color(0xFF64748B)
                        )
                    ) {
                        if (aplicando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Aplicando...",
                                color = Color.White
                            )
                        } else {
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
                                text = vacante.empresa,
                                style = MaterialTheme.typography.titleLarge,
                                color = textoPrincipal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = vacante.puesto,
                                color = textoSecundario
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = if (vacante.disponible) {
                                    "Vacante disponible"
                                } else {
                                    "Vacante no disponible"
                                },
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
                    dato = vacante.salario.ifBlank { "No especificado" },
                    icono = Icons.Default.MonetizationOn,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                InfoVacanteMiniCard(
                    titulo = "Ubicación",
                    dato = direccionVacante,
                    icono = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            InfoVacanteCard(
                titulo = "Horario",
                dato = vacante.horario.ifBlank { "No especificado" },
                icono = Icons.Default.Schedule
            )

            InfoVacanteCard(
                titulo = "Dirección",
                dato = direccionVacante,
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
                    text = descripcionVacante,
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

            RequisitoItem(requisitosVacante)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Beneficios",
                style = MaterialTheme.typography.titleMedium,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequisitoItem(beneficiosVacante)

            Spacer(modifier = Modifier.height(18.dp))

            InfoVacanteCard(
                titulo = "Contacto",
                dato = vacante.telefono
                    .takeIf { it.isNotBlank() }
                    ?: "El negocio recibirá tu postulación desde la app.",
                icono = Icons.Default.Call
            )

            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    if (mensajeDialogo.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {
                tituloDialogo = ""
                mensajeDialogo = ""
            },
            containerColor = Color(0xFF334155),
            title = {
                Text(
                    text = tituloDialogo,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = mensajeDialogo,
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        tituloDialogo = ""
                        mensajeDialogo = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED)
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        color = Color.White
                    )
                }
            }
        )
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