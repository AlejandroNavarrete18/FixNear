package com.example.fixnearv1.iuu.screens

import android.preference.PreferenceManager
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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fixnearv1.modelo.EmpleoDemo
import com.example.fixnearv1.modelo.ui.theme.*
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SupabaseApi
import com.example.fixnearv1.utils.Vacante
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleosScreen(
    onRegresar: () -> Unit,
    onVerDetalle: (Vacante) -> Unit
){
    val context = LocalContext.current

    var busqueda by remember {
        mutableStateOf("")
    }

    var vacantes by remember {
        mutableStateOf<List<Vacante>>(emptyList())
    }

    var vacanteSeleccionada by remember {
        mutableStateOf<Vacante?>(null)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val token = SesionLocal.obtenerAccessToken(context)

        if (token.isBlank()) {
            error = "No hay sesión activa. Inicia sesión nuevamente."
            cargando = false
            return@LaunchedEffect
        }

        val resultado = SupabaseApi.obtenerVacantesDisponibles(
            accessToken = token
        )

        resultado.onSuccess { lista ->
            vacantes = lista
        }

        resultado.onFailure { exception ->
            error = exception.message ?: "No se pudieron cargar las vacantes."
        }

        cargando = false
    }

    val vacantesFiltradas = vacantes.filter { vacante ->
        val texto = "${vacante.empresa} ${vacante.puesto} ${vacante.direccion}"
        texto.contains(busqueda, ignoreCase = true)
    }

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
                            .height(360.dp)
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
                            VacantesMapInteractivo(
                                vacantes = vacantesFiltradas,
                                vacanteSeleccionada = vacanteSeleccionada,
                                onVacanteSeleccionada = { vacante ->
                                    vacanteSeleccionada = vacante
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = {
                                busqueda = it
                            },
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
                                    "Toca un marcador para ver la vacante",
                                    color = TextoPrincipal
                                )
                            }
                        }
                    }

                    vacanteSeleccionada?.let { vacante ->
                        Spacer(modifier = Modifier.height(14.dp))

                        VacanteSeleccionadaCard(
                            vacante = vacante,
                            onVerVacante = {
                                onVerDetalle(vacante)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Vacantes disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (cargando) {
                        Text(
                            text = "Cargando vacantes reales...",
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

                    if (!cargando && vacantesFiltradas.isEmpty()) {
                        Text(
                            text = "No hay vacantes disponibles por el momento.",
                            color = TextoSecundario
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            items(
                items = vacantesFiltradas,
                key = { it.id }
            ) { vacante ->
                VacanteCardMapaMejorada(
                    vacante = vacante,
                    seleccionada = vacanteSeleccionada?.id == vacante.id,
                    onSeleccionar = {
                        vacanteSeleccionada = vacante
                    },
                    onVerVacante = {
                        onVerDetalle(vacante)
                    }
                )
            }
        }
    }
}

@Composable
fun VacantesMapInteractivo(
    vacantes: List<Vacante>,
    vacanteSeleccionada: Vacante?,
    onVacanteSeleccionada: (Vacante) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val ubicacionInicial = GeoPoint(
        24.8091,
        -107.3940
    )

    val mapView = remember {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )

        Configuration.getInstance().userAgentValue =
            context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)

            minZoomLevel = 12.0
            maxZoomLevel = 19.0

            controller.setZoom(13.8)
            controller.setCenter(ubicacionInicial)
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView
        },
        update = { mapa ->
            mapa.overlays.clear()

            vacantes.forEach { vacante ->
                val latitud = vacante.latitud
                val longitud = vacante.longitud

                if (latitud != null && longitud != null) {
                    val marcador = Marker(mapa).apply {
                        position = GeoPoint(latitud, longitud)
                        title = vacante.empresa
                        snippet = "${vacante.puesto} · ${vacante.salario}"

                        setAnchor(
                            Marker.ANCHOR_CENTER,
                            Marker.ANCHOR_BOTTOM
                        )

                        setOnMarkerClickListener { marker, map ->
                            onVacanteSeleccionada(vacante)
                            map.controller.animateTo(marker.position)
                            marker.showInfoWindow()
                            true
                        }
                    }

                    mapa.overlays.add(marcador)
                }
            }

            vacanteSeleccionada?.let { vacante ->
                val latitud = vacante.latitud
                val longitud = vacante.longitud

                if (latitud != null && longitud != null) {
                    mapa.controller.animateTo(
                        GeoPoint(latitud, longitud)
                    )
                }
            }

            mapa.invalidate()
        }
    )
}

@Composable
fun VacanteSeleccionadaCard(
    vacante: Vacante,
    onVerVacante: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    tint = MoradoClaro
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Vacante seleccionada",
                    color = TextoPrincipal,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = vacante.empresa,
                color = TextoPrincipal,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Puesto: ${vacante.puesto}",
                color = TextoSecundario
            )

            Text(
                text = "Salario: ${vacante.salario}",
                color = TextoSecundario
            )

            Text(
                text = "Dirección: ${vacante.direccion}",
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onVerVacante,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoPrincipal
                )
            ) {
                Icon(
                    Icons.Default.Work,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Ver y aplicar",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun VacanteCardMapaMejorada(
    vacante: Vacante,
    seleccionada: Boolean,
    onSeleccionar: () -> Unit,
    onVerVacante: () -> Unit
) {
    val colorCard = if (seleccionada) {
        CardOscura2
    } else {
        CardOscura
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = onSeleccionar
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
                    text = vacante.empresa,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Vacante: ${vacante.puesto}",
                color = TextoSecundario
            )

            Text(
                text = "Salario: ${vacante.salario}",
                color = TextoSecundario
            )

            Text(
                text = "Horario: ${vacante.horario}",
                color = TextoSecundario
            )

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

                Text(
                    text = vacante.direccion.ifBlank {
                        "Ubicación registrada"
                    },
                    color = TextoSecundario
                )
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
                Text(
                    "Ver vacante",
                    color = Color.White
                )
            }
        }
    }
}

private fun Vacante.toEmpleoDemo(): EmpleoDemo {
    return EmpleoDemo(
        empresa = empresa,
        puesto = puesto,
        salario = salario,
        horario = horario,
        distancia = direccion.ifBlank {
            "Ubicación registrada"
        }
    )
}