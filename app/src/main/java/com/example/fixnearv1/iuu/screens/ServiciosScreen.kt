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
import androidx.compose.foundation.BorderStroke
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
import androidx.core.content.ContextCompat
import com.example.fixnearv1.modelo.ui.theme.*
import com.example.fixnearv1.ui.components.FixNearMap
import com.example.fixnearv1.ui.components.TipoMapaFixNear
import com.example.fixnearv1.utils.PerfilUsuario
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SupabaseApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    onRegresar: () -> Unit,
    onVerPerfilTrabajador: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var busqueda by remember {
        mutableStateOf("")
    }

    var perfilCliente by remember {
        mutableStateOf<PerfilUsuario?>(null)
    }

    var trabajadores by remember {
        mutableStateOf<List<PerfilUsuario>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }

    var mensajeDialogo by remember {
        mutableStateOf("")
    }

    var tituloDialogo by remember {
        mutableStateOf("")
    }

    var trabajadorEnviandoId by remember {
        mutableStateOf("")
    }

    var trabajadorPendientePermiso by remember {
        mutableStateOf<PerfilUsuario?>(null)
    }

    fun enviarSolicitudReal(trabajador: PerfilUsuario) {
        val cliente = perfilCliente

        if (cliente == null) {
            tituloDialogo = "Error"
            mensajeDialogo = "No se pudo cargar tu perfil de usuario."
            return
        }

        val userId = SesionLocal.obtenerUserId(context)
        val token = SesionLocal.obtenerAccessToken(context)

        if (userId.isBlank() || token.isBlank()) {
            tituloDialogo = "Sesión no válida"
            mensajeDialogo = "Inicia sesión nuevamente."
            return
        }

        val ubicacion = obtenerUltimaUbicacionReal(context)

        if (ubicacion == null) {
            tituloDialogo = "Ubicación no disponible"
            mensajeDialogo =
                "Activa la ubicación del dispositivo y vuelve a intentarlo."
            return
        }

        trabajadorEnviandoId = trabajador.id

        scope.launch {
            val servicio = trabajador.oficio
                .takeIf { it.isNotBlank() }
                ?: "Servicio general"

            val descripcion = trabajador.descripcion
                .takeIf { it.isNotBlank() }
                ?: "Solicitud enviada desde Servicios Cercanos."

            val resultado = SupabaseApi.crearSolicitud(
                accessToken = token,
                clienteId = userId,
                trabajadorId = trabajador.id,
                servicio = servicio,
                descripcion = descripcion,
                nombreCliente = cliente.nombre,
                telefonoCliente = cliente.telefono,
                nombreTrabajador = trabajador.nombre,
                telefonoTrabajador = trabajador.telefono,
                latitudCliente = ubicacion.latitude,
                longitudCliente = ubicacion.longitude,
                direccionCliente = "Ubicación GPS: ${ubicacion.latitude}, ${ubicacion.longitude}"
            )

            trabajadorEnviandoId = ""

            resultado.onSuccess {
                tituloDialogo = "Solicitud enviada"
                mensajeDialogo =
                    "Tu solicitud fue enviada a ${trabajador.nombre}."
            }

            resultado.onFailure { exception ->
                tituloDialogo = "No se pudo enviar"
                mensajeDialogo =
                    exception.message ?: "Ocurrió un error al crear la solicitud."
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
                trabajadorPendientePermiso?.let { trabajador ->
                    enviarSolicitudReal(trabajador)
                }
            } else {
                tituloDialogo = "Permiso requerido"
                mensajeDialogo =
                    "Necesitas permitir la ubicación para crear una solicitud real."
            }

            trabajadorPendientePermiso = null
        }

    fun solicitarServicio(trabajador: PerfilUsuario) {
        if (!tienePermisoUbicacion(context)) {
            trabajadorPendientePermiso = trabajador

            permisosUbicacionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        enviarSolicitudReal(trabajador)
    }

    LaunchedEffect(Unit) {
        val userId = SesionLocal.obtenerUserId(context)
        val token = SesionLocal.obtenerAccessToken(context)

        if (userId.isBlank() || token.isBlank()) {
            error = "No hay sesión activa."
            cargando = false
            return@LaunchedEffect
        }

        val perfilResultado = SupabaseApi.obtenerPerfil(
            userId = userId,
            accessToken = token
        )

        perfilResultado.onSuccess {
            perfilCliente = it
        }

        perfilResultado.onFailure {
            error = it.message ?: "No se pudo cargar tu perfil."
        }

        val trabajadoresResultado =
            SupabaseApi.obtenerTrabajadoresDisponibles(token)

        trabajadoresResultado.onSuccess { lista ->
            trabajadores = lista.filter { trabajador ->
                trabajador.id != userId
            }
        }

        trabajadoresResultado.onFailure {
            error = it.message ?: "No se pudieron cargar trabajadores."
        }

        cargando = false
    }

    val trabajadoresFiltrados = trabajadores.filter { trabajador ->
        val texto = "${trabajador.nombre} ${trabajador.oficio} ${trabajador.descripcion}"
        texto.contains(busqueda, ignoreCase = true)
    }

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
                            value = busqueda,
                            onValueChange = {
                                busqueda = it
                            },
                            placeholder = {
                                Text(
                                    "Buscar trabajador o servicio...",
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
                            ),
                            singleLine = true
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
                                    "Trabajadores reales disponibles",
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

                    if (cargando) {
                        Text(
                            text = "Cargando trabajadores...",
                            color = TextoSecundario
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

                    if (!cargando && trabajadoresFiltrados.isEmpty()) {
                        Text(
                            text = "No hay trabajadores disponibles por el momento.",
                            color = TextoSecundario
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            items(
                items = trabajadoresFiltrados,
                key = { it.id }
            ) { trabajador ->
                ServicioCardMapaMejorada(
                    trabajador = trabajador,
                    enviando = trabajadorEnviandoId == trabajador.id,
                    onVerPerfil = onVerPerfilTrabajador,
                    onMapa = {
                        val consulta = Uri.encode(
                            "${trabajador.nombre} ${trabajador.oficio}"
                        )

                        val uri = Uri.parse("geo:0,0?q=$consulta")
                        val intent = Intent(Intent.ACTION_VIEW, uri)

                        runCatching {
                            context.startActivity(intent)
                        }
                    },
                    onSolicitar = {
                        solicitarServicio(trabajador)
                    }
                )
            }
        }
    }

    if (mensajeDialogo.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {
                mensajeDialogo = ""
                tituloDialogo = ""
            },
            containerColor = CardOscura,
            title = {
                Text(
                    tituloDialogo,
                    color = TextoPrincipal
                )
            },
            text = {
                Text(
                    mensajeDialogo,
                    color = TextoSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mensajeDialogo = ""
                        tituloDialogo = ""
                    },
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
    trabajador: PerfilUsuario,
    enviando: Boolean,
    onVerPerfil: () -> Unit,
    onMapa: () -> Unit,
    onSolicitar: () -> Unit
) {
    val oficio = trabajador.oficio
        .takeIf { it.isNotBlank() }
        ?: "Servicio general"

    val descripcion = trabajador.descripcion
        .takeIf { it.isNotBlank() }
        ?: "Trabajador disponible para solicitudes."

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardOscura
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = oficio,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trabajador: ${trabajador.nombre}",
                color = TextoSecundario
            )

            Text(
                text = "Teléfono: ${
                    trabajador.telefono.takeIf { it.isNotBlank() } ?: "No registrado"
                }",
                color = TextoSecundario
            )

            Text(
                text = "Descripción: $descripcion",
                color = TextoSecundario
            )

            Text(
                text = "Estado: Disponible",
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(12.dp))

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        "Trabajador verificado",
                        color = TextoPrincipal
                    )
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
                    enabled = !enviando,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoradoPrincipal
                    )
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Solicitar",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun tienePermisoUbicacion(
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
private fun obtenerUltimaUbicacionReal(
    context: Context
): Location? {
    if (!tienePermisoUbicacion(context)) {
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