package com.example.fixnearv1.iuu.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fixnearv1.utils.PerfilUsuario
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SolicitudServicio
import com.example.fixnearv1.utils.SupabaseApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrabajadorHomeScreen(
    onRegresar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var perfil by remember {
        mutableStateOf<PerfilUsuario?>(null)
    }

    var solicitudes by remember {
        mutableStateOf<List<SolicitudServicio>>(emptyList())
    }

    var disponible by remember {
        mutableStateOf(false)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }

    var tituloDialogo by remember {
        mutableStateOf("")
    }

    var mensajeDialogo by remember {
        mutableStateOf("")
    }

    var solicitudAceptandoId by remember {
        mutableStateOf("")
    }

    var solicitudPendientePermiso by remember {
        mutableStateOf<SolicitudServicio?>(null)
    }

    fun cargarDatos() {
        scope.launch {
            cargando = true
            error = ""

            val userId = SesionLocal.obtenerUserId(context)
            val token = SesionLocal.obtenerAccessToken(context)

            if (userId.isBlank() || token.isBlank()) {
                error = "No hay sesión activa."
                cargando = false
                return@launch
            }

            val perfilResultado = SupabaseApi.obtenerPerfil(
                userId = userId,
                accessToken = token
            )

            perfilResultado.onSuccess { perfilUsuario ->
                perfil = perfilUsuario
                disponible = perfilUsuario.disponible
            }

            perfilResultado.onFailure { exception ->
                error = exception.message ?: "No se pudo cargar tu perfil."
            }

            val solicitudesResultado = SupabaseApi.obtenerSolicitudesPendientes(
                accessToken = token,
                trabajadorId = userId
            )

            solicitudesResultado.onSuccess { lista ->
                solicitudes = lista
            }

            solicitudesResultado.onFailure { exception ->
                error = exception.message ?: "No se pudieron cargar las solicitudes."
            }

            cargando = false
        }
    }

    fun abrirMapaSolicitud(solicitud: SolicitudServicio) {
        val latitud = solicitud.latitudCliente
        val longitud = solicitud.longitudCliente

        if (latitud == null || longitud == null) {
            tituloDialogo = "Ubicación no disponible"
            mensajeDialogo = "Esta solicitud no tiene ubicación del cliente."
            return
        }

        val etiqueta = Uri.encode(
            "Cliente: ${solicitud.nombreCliente}"
        )

        val uri = Uri.parse(
            "geo:$latitud,$longitud?q=$latitud,$longitud($etiqueta)"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)

        runCatching {
            context.startActivity(intent)
        }.onFailure {
            tituloDialogo = "No se pudo abrir el mapa"
            mensajeDialogo = "No hay una aplicación de mapas disponible."
        }
    }

    fun aceptarSolicitudReal(solicitud: SolicitudServicio) {
        val userId = SesionLocal.obtenerUserId(context)
        val token = SesionLocal.obtenerAccessToken(context)

        if (userId.isBlank() || token.isBlank()) {
            tituloDialogo = "Sesión no válida"
            mensajeDialogo = "Inicia sesión nuevamente."
            return
        }

        val ubicacionTrabajador =
            obtenerUltimaUbicacionRealTrabajador(context)

        if (ubicacionTrabajador == null) {
            tituloDialogo = "Ubicación no disponible"
            mensajeDialogo =
                "Activa la ubicación del dispositivo para aceptar la solicitud."
            return
        }

        solicitudAceptandoId = solicitud.id

        scope.launch {
            val resultado = SupabaseApi.aceptarSolicitud(
                accessToken = token,
                solicitudId = solicitud.id,
                trabajadorId = userId,
                latitudTrabajador = ubicacionTrabajador.latitude,
                longitudTrabajador = ubicacionTrabajador.longitude
            )

            solicitudAceptandoId = ""

            resultado.onSuccess {
                solicitudes = solicitudes.filter { item ->
                    item.id != solicitud.id
                }

                tituloDialogo = "Solicitud aceptada"
                mensajeDialogo =
                    "Aceptaste el servicio de ${solicitud.servicio} para ${solicitud.nombreCliente}."
            }

            resultado.onFailure { exception ->
                tituloDialogo = "No se pudo aceptar"
                mensajeDialogo =
                    exception.message ?: "Ocurrió un error al aceptar la solicitud."
            }
        }
    }

    val permisosUbicacionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->
            val concedido =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (concedido) {
                solicitudPendientePermiso?.let { solicitud ->
                    aceptarSolicitudReal(solicitud)
                }
            } else {
                tituloDialogo = "Permiso requerido"
                mensajeDialogo =
                    "Necesitas permitir la ubicación para aceptar solicitudes reales."
            }

            solicitudPendientePermiso = null
        }

    fun solicitarAceptar(solicitud: SolicitudServicio) {
        if (!tienePermisoUbicacionTrabajador(context)) {
            solicitudPendientePermiso = solicitud

            permisosUbicacionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        aceptarSolicitudReal(solicitud)
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val nombreTrabajador = perfil?.nombre
        ?.takeIf { it.isNotBlank() }
        ?: "Trabajador"

    val oficioTrabajador = perfil?.oficio
        ?.takeIf { it.isNotBlank() }
        ?: "Trabajador"

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel del trabajador",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            cargarDatos()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = "Actualizar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF334155)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(70.dp),
                            shape = CircleShape,
                            color = Color(0xFF0F172A)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(58.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = nombreTrabajador,
                                color = Color.White
                            )

                            Text(
                                text = "$oficioTrabajador verificado",
                                color = Color.LightGray
                            )

                            Text(
                                text = "⭐ 4.9 • Trabajos reales",
                                color = Color.LightGray
                            )
                        }

                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF334155)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Estado de disponibilidad",
                                color = Color.White
                            )

                            Text(
                                text = if (disponible) {
                                    "Disponible para recibir trabajos"
                                } else {
                                    "Fuera de servicio"
                                },
                                color = Color.LightGray
                            )
                        }

                        Switch(
                            checked = disponible,
                            onCheckedChange = {
                                disponible = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF7C3AED),
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = Color(0xFF64748B)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GananciaCard(
                        titulo = "Hoy",
                        valor = "$0",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    GananciaCard(
                        titulo = "Semana",
                        valor = "$0",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Solicitudes cercanas",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (cargando) {
                    Text(
                        text = "Cargando solicitudes...",
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (!cargando && solicitudes.isEmpty()) {
                    Text(
                        text = "No tienes solicitudes pendientes por el momento.",
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            items(
                items = solicitudes,
                key = { it.id }
            ) { solicitud ->
                SolicitudCard(
                    solicitud = solicitud,
                    aceptando = solicitudAceptandoId == solicitud.id,
                    onMapa = {
                        abrirMapaSolicitud(solicitud)
                    },
                    onAceptar = {
                        solicitarAceptar(solicitud)
                    }
                )
            }
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
fun GananciaCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                color = Color.LightGray
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun SolicitudCard(
    solicitud: SolicitudServicio,
    aceptando: Boolean,
    onMapa: () -> Unit,
    onAceptar: () -> Unit
) {
    val distancia = solicitud.distanciaKm?.let {
        "$it km"
    } ?: "Por calcular"

    val tiempo = solicitud.duracionMin?.let {
        "${it.toInt()} min"
    } ?: "Por calcular"

    val cliente = solicitud.nombreCliente
        .takeIf { it.isNotBlank() }
        ?: "Cliente"

    val telefono = solicitud.telefonoCliente
        .takeIf { it.isNotBlank() }
        ?: "Sin teléfono"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = solicitud.servicio.takeIf { it.isNotBlank() }
                    ?: "Servicio solicitado",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cliente: $cliente",
                color = Color.LightGray
            )

            Text(
                text = "Teléfono: $telefono",
                color = Color.LightGray
            )

            Text(
                text = "Distancia: $distancia",
                color = Color.LightGray
            )

            Text(
                text = "Llegada estimada: $tiempo",
                color = Color.LightGray
            )

            if (solicitud.direccionCliente.isNotBlank()) {
                Text(
                    text = "Dirección: ${solicitud.direccionCliente}",
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                OutlinedButton(
                    onClick = onMapa,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFB388FF)
                    )
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Mapa")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onAceptar,
                    enabled = !aceptando,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED)
                    )
                ) {
                    if (aceptando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Aceptar",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun tienePermisoUbicacionTrabajador(
    context: Context
): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fine || coarse
}

@SuppressLint("MissingPermission")
private fun obtenerUltimaUbicacionRealTrabajador(
    context: Context
): Location? {
    if (!tienePermisoUbicacionTrabajador(context)) {
        return null
    }

    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val proveedores = locationManager.getProviders(true)

    return proveedores
        .mapNotNull { proveedor ->
            runCatching {
                locationManager.getLastKnownLocation(proveedor)
            }.getOrNull()
        }
        .maxByOrNull { ubicacion ->
            ubicacion.time
        }
}