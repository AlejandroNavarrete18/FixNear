package com.example.fixnearv1.ui.components

import android.preference.PreferenceManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URLEncoder

enum class TipoMapaFixNear {
    SERVICIOS,
    EMPLEOS
}

data class LugarMapa(
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double
)

@Composable
fun FixNearMap(
    modifier: Modifier = Modifier,
    tipoMapa: TipoMapaFixNear = TipoMapaFixNear.SERVICIOS
) {
    val context = LocalContext.current

    val ubicacionInicial = GeoPoint(24.8091, -107.3940)

    var lugares by remember {
        mutableStateOf<List<LugarMapa>>(emptyList())
    }

    LaunchedEffect(tipoMapa) {
        lugares = obtenerLugaresReales(
            tipoMapa = tipoMapa,
            latitud = ubicacionInicial.latitude,
            longitud = ubicacionInicial.longitude
        )
    }

    val mapView = remember {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )

        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)

            minZoomLevel = 12.0
            maxZoomLevel = 19.0

            controller.setZoom(13.5)
            controller.setCenter(ubicacionInicial)
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()

        onDispose {
            mapView.onPause()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView
        },
        update = { mapa ->

            mapa.overlays.clear()

            lugares.forEach { lugar ->

                val marcador = Marker(mapa).apply {
                    position = GeoPoint(
                        lugar.latitud,
                        lugar.longitud
                    )

                    title = lugar.nombre

                    snippet = lugar.categoria

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )
                }

                mapa.overlays.add(marcador)
            }

            mapa.invalidate()
        }
    )
}

private suspend fun obtenerLugaresReales(
    tipoMapa: TipoMapaFixNear,
    latitud: Double,
    longitud: Double
): List<LugarMapa> {
    return withContext(Dispatchers.IO) {
        try {
            val consulta = construirConsultaOverpass(
                tipoMapa = tipoMapa,
                latitud = latitud,
                longitud = longitud
            )

            val consultaCodificada = URLEncoder.encode(
                consulta,
                "UTF-8"
            )

            val url =
                "https://overpass-api.de/api/interpreter?data=$consultaCodificada"

            val cliente = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .header(
                    "User-Agent",
                    "FixNearV1/1.0"
                )
                .build()

            cliente.newCall(request).execute().use { response ->

                if (!response.isSuccessful) {
                    return@withContext emptyList()
                }

                val cuerpo = response.body?.string()
                    ?: return@withContext emptyList()

                val json = JSONObject(cuerpo)

                val elementos = json.optJSONArray("elements")
                    ?: return@withContext emptyList()

                val resultado = mutableListOf<LugarMapa>()

                for (i in 0 until elementos.length()) {

                    val elemento = elementos.getJSONObject(i)
                    val tags = elemento.optJSONObject("tags")

                    val nombre = tags
                        ?.optString("name")
                        ?.takeIf { it.isNotBlank() }
                        ?: continue

                    val latLugar: Double
                    val lonLugar: Double

                    if (elemento.has("lat") && elemento.has("lon")) {
                        latLugar = elemento.getDouble("lat")
                        lonLugar = elemento.getDouble("lon")
                    } else {
                        val centro = elemento.optJSONObject("center")
                            ?: continue

                        latLugar = centro.optDouble("lat")
                        lonLugar = centro.optDouble("lon")
                    }

                    val categoria = obtenerCategoria(tags)

                    resultado.add(
                        LugarMapa(
                            nombre = nombre,
                            categoria = categoria,
                            latitud = latLugar,
                            longitud = lonLugar
                        )
                    )
                }

                return@withContext resultado.take(30)
            }

        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }
}

private fun construirConsultaOverpass(
    tipoMapa: TipoMapaFixNear,
    latitud: Double,
    longitud: Double
): String {
    val radio = 5000

    return when (tipoMapa) {

        TipoMapaFixNear.SERVICIOS -> {
            """
            [out:json][timeout:25];
            (
              node(around:$radio,$latitud,$longitud)["shop"~"hardware|electronics|car_repair|doityourself"];
              way(around:$radio,$latitud,$longitud)["shop"~"hardware|electronics|car_repair|doityourself"];
              relation(around:$radio,$latitud,$longitud)["shop"~"hardware|electronics|car_repair|doityourself"];

              node(around:$radio,$latitud,$longitud)["craft"~"electrician|plumber|carpenter|painter|mechanic"];
              way(around:$radio,$latitud,$longitud)["craft"~"electrician|plumber|carpenter|painter|mechanic"];
              relation(around:$radio,$latitud,$longitud)["craft"~"electrician|plumber|carpenter|painter|mechanic"];

              node(around:$radio,$latitud,$longitud)["amenity"~"clinic|pharmacy|hospital|car_wash"];
              way(around:$radio,$latitud,$longitud)["amenity"~"clinic|pharmacy|hospital|car_wash"];
              relation(around:$radio,$latitud,$longitud)["amenity"~"clinic|pharmacy|hospital|car_wash"];
            );
            out center 30;
            """.trimIndent()
        }

        TipoMapaFixNear.EMPLEOS -> {
            """
            [out:json][timeout:25];
            (
              node(around:$radio,$latitud,$longitud)["shop"];
              way(around:$radio,$latitud,$longitud)["shop"];
              relation(around:$radio,$latitud,$longitud)["shop"];

              node(around:$radio,$latitud,$longitud)["office"];
              way(around:$radio,$latitud,$longitud)["office"];
              relation(around:$radio,$latitud,$longitud)["office"];

              node(around:$radio,$latitud,$longitud)["amenity"~"restaurant|cafe|fast_food|bank|pharmacy|clinic|hospital|marketplace"];
              way(around:$radio,$latitud,$longitud)["amenity"~"restaurant|cafe|fast_food|bank|pharmacy|clinic|hospital|marketplace"];
              relation(around:$radio,$latitud,$longitud)["amenity"~"restaurant|cafe|fast_food|bank|pharmacy|clinic|hospital|marketplace"];

              node(around:$radio,$latitud,$longitud)["tourism"~"hotel|motel"];
              way(around:$radio,$latitud,$longitud)["tourism"~"hotel|motel"];
              relation(around:$radio,$latitud,$longitud)["tourism"~"hotel|motel"];
            );
            out center 30;
            """.trimIndent()
        }
    }
}

private fun obtenerCategoria(
    tags: JSONObject?
): String {
    if (tags == null) {
        return "Lugar"
    }

    return when {
        tags.has("shop") -> {
            "Comercio: ${tags.optString("shop")}"
        }

        tags.has("amenity") -> {
            "Servicio: ${tags.optString("amenity")}"
        }

        tags.has("craft") -> {
            "Oficio: ${tags.optString("craft")}"
        }

        tags.has("office") -> {
            "Oficina: ${tags.optString("office")}"
        }

        tags.has("tourism") -> {
            "Hotel o turismo: ${tags.optString("tourism")}"
        }

        else -> {
            "Lugar cercano"
        }
    }
}