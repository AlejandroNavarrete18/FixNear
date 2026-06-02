package com.example.fixnearv1.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object SupabaseApi {

    private const val SUPABASE_URL =
        "https://qcpyjgkmgeswumokocwu.supabase.co"

    private const val SUPABASE_KEY =
        "sb_publishable_mvuX_ps9zsgIwVopSVGcsA_k7ieRRvT"

    private val client = OkHttpClient()

    private val jsonMediaType =
        "application/json; charset=utf-8".toMediaType()

    // ─── URL para OAuth con Google ───────────────────────────────
    fun obtenerUrlGoogleOAuth(): String {
        return "$SUPABASE_URL/auth/v1/authorize" +
                "?provider=google" +
                "&redirect_to=fixnear://auth/callback"
    }

    // ─── Intercambiar código OAuth por sesión ───────────────────
    suspend fun intercambiarCodigoOAuth(
        accessToken: String,
        refreshToken: String,
        userId: String,
        correo: String
    ): Result<SesionSupabase> {
        return Result.success(
            SesionSupabase(
                userId = userId,
                correo = correo,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    // ─── Registrar usuario ──────────────────────────────────────
    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        telefono: String,
        password: String,
        tipoUsuario: String,
        oficio: String = "",
        descripcion: String = "",
        disponible: Boolean = false
    ): Result<SesionSupabase> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("email", correo)
                    .put("password", password)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/signup")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al registrar usuario: $responseText")
                        )
                    }

                    val json = JSONObject(responseText)

                    val user = json.optJSONObject("user")
                    val userIdRegistro = user?.optString("id").orEmpty()
                    val emailRegistro = user?.optString("email").orEmpty()

                    val session = json.optJSONObject("session")
                    var accessToken =
                        session?.optString("access_token").orEmpty()

                    val refreshToken =
                        session?.optString("refresh_token").orEmpty()

                    var sesionFinal = SesionSupabase(
                        userId = userIdRegistro,
                        correo = emailRegistro.ifBlank { correo },
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )

                    if (accessToken.isBlank() || sesionFinal.userId.isBlank()) {
                        val loginResultado = iniciarSesion(
                            correo = correo,
                            password = password
                        )

                        if (loginResultado.isFailure) {
                            return@withContext Result.failure(
                                Exception(
                                    "Cuenta creada, pero no se pudo iniciar sesión. Intenta iniciar sesión manualmente."
                                )
                            )
                        }

                        sesionFinal = loginResultado.getOrThrow()
                        accessToken = sesionFinal.accessToken
                    }

                    val perfilCreado = crearPerfil(
                        accessToken = accessToken,
                        userId = sesionFinal.userId,
                        nombre = nombre,
                        correo = sesionFinal.correo.ifBlank { correo },
                        telefono = telefono,
                        tipoUsuario = tipoUsuario,
                        oficio = oficio,
                        descripcion = descripcion,
                        disponible = disponible
                    )

                    if (perfilCreado.isFailure) {
                        return@withContext Result.failure(
                            perfilCreado.exceptionOrNull()
                                ?: Exception("No se pudo crear el perfil.")
                        )
                    }

                    Result.success(sesionFinal)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Iniciar sesión ─────────────────────────────────────────
    suspend fun iniciarSesion(
        correo: String,
        password: String
    ): Result<SesionSupabase> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("email", correo)
                    .put("password", password)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/token?grant_type=password")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Correo o contraseña incorrectos.")
                        )
                    }

                    val json = JSONObject(responseText)

                    val accessToken = json.optString("access_token")
                    val refreshToken = json.optString("refresh_token")
                    val user = json.optJSONObject("user")

                    val userId = user?.optString("id").orEmpty()
                    val email = user?.optString("email").orEmpty()

                    if (userId.isBlank() || accessToken.isBlank()) {
                        return@withContext Result.failure(
                            Exception("No se pudo obtener la sesión.")
                        )
                    }

                    Result.success(
                        SesionSupabase(
                            userId = userId,
                            correo = email,
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    )
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Refrescar sesión ───────────────────────────────────────
    suspend fun refrescarSesion(
        refreshToken: String
    ): Result<SesionSupabase> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("refresh_token", refreshToken)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/token?grant_type=refresh_token")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Sesión expirada. Inicia sesión de nuevo.")
                        )
                    }

                    val json = JSONObject(responseText)

                    val accessToken = json.optString("access_token")
                    val newRefresh = json.optString("refresh_token")
                    val user = json.optJSONObject("user")

                    val userId = user?.optString("id").orEmpty()
                    val email = user?.optString("email").orEmpty()

                    if (userId.isBlank() || accessToken.isBlank()) {
                        return@withContext Result.failure(
                            Exception("No se pudo renovar la sesión.")
                        )
                    }

                    Result.success(
                        SesionSupabase(
                            userId = userId,
                            correo = email,
                            accessToken = accessToken,
                            refreshToken = newRefresh
                        )
                    )
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Crear perfil ───────────────────────────────────────────
    private suspend fun crearPerfil(
        accessToken: String,
        userId: String,
        nombre: String,
        correo: String,
        telefono: String,
        tipoUsuario: String,
        oficio: String,
        descripcion: String,
        disponible: Boolean
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("id", userId)
                    .put("nombre", nombre)
                    .put("correo", correo)
                    .put("telefono", telefono)
                    .put("tipo_usuario", tipoUsuario)
                    .put("oficio", oficio)
                    .put("descripcion", descripcion)
                    .put("disponible", disponible)
                    .put("latitud", 24.8091)
                    .put("longitud", -107.3940)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/perfiles?on_conflict=id")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader(
                        "Prefer",
                        "resolution=merge-duplicates,return=minimal"
                    )
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al crear perfil: $responseText")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Obtener perfil ─────────────────────────────────────────
    suspend fun obtenerPerfil(
        userId: String,
        accessToken: String
    ): Result<PerfilUsuario> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/perfiles?id=eq.$userId&select=*")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al obtener perfil: $responseText")
                        )
                    }

                    val array = JSONArray(responseText)

                    if (array.length() == 0) {
                        return@withContext Result.failure(
                            Exception("No existe perfil para este usuario.")
                        )
                    }

                    val item = array.getJSONObject(0)

                    Result.success(
                        PerfilUsuario(
                            id = item.optString("id"),
                            nombre = item.optString("nombre"),
                            correo = item.optString("correo"),
                            telefono = item.optString("telefono"),
                            tipoUsuario = item.optString("tipo_usuario"),
                            oficio = item.optString("oficio"),
                            descripcion = item.optString("descripcion"),
                            disponible = item.optBoolean("disponible")
                        )
                    )
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Obtener trabajadores disponibles ───────────────────────
    suspend fun obtenerTrabajadoresDisponibles(
        accessToken: String
    ): Result<List<PerfilUsuario>> {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    "$SUPABASE_URL/rest/v1/perfiles" +
                            "?tipo_usuario=eq.Trabajador" +
                            "&disponible=eq.true" +
                            "&select=*" +
                            "&order=created_at.desc"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al obtener trabajadores: $responseText")
                        )
                    }

                    val array = JSONArray(responseText)
                    val trabajadores = mutableListOf<PerfilUsuario>()

                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)

                        trabajadores.add(
                            PerfilUsuario(
                                id = item.optString("id"),
                                nombre = item.optString("nombre"),
                                correo = item.optString("correo"),
                                telefono = item.optString("telefono"),
                                tipoUsuario = item.optString("tipo_usuario"),
                                oficio = item.optString("oficio"),
                                descripcion = item.optString("descripcion"),
                                disponible = item.optBoolean("disponible")
                            )
                        )
                    }

                    Result.success(trabajadores)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Obtener vacantes disponibles ───────────────────────────
    suspend fun obtenerVacantesDisponibles(
        accessToken: String
    ): Result<List<Vacante>> {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    "$SUPABASE_URL/rest/v1/vacantes" +
                            "?disponible=eq.true" +
                            "&select=*" +
                            "&order=created_at.desc"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al obtener vacantes: $responseText")
                        )
                    }

                    val array = JSONArray(responseText)
                    val vacantes = mutableListOf<Vacante>()

                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)

                        vacantes.add(
                            Vacante(
                                id = item.optString("id"),
                                empresa = item.optString("empresa"),
                                puesto = item.optString("puesto"),
                                salario = item.optString("salario"),
                                horario = item.optString("horario"),
                                descripcion = item.optString("descripcion"),
                                requisitos = item.optString("requisitos"),
                                beneficios = item.optString("beneficios"),
                                telefono = item.optString("telefono"),
                                direccion = item.optString("direccion"),
                                latitud = obtenerDoubleNullable(item, "latitud"),
                                longitud = obtenerDoubleNullable(item, "longitud"),
                                disponible = item.optBoolean("disponible")
                            )
                        )
                    }

                    Result.success(vacantes)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Actualizar nombre ──────────────────────────────────────
    suspend fun actualizarNombrePerfil(
        userId: String,
        accessToken: String,
        nuevoNombre: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("nombre", nuevoNombre)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/perfiles?id=eq.$userId")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .patch(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val txt = response.body?.string().orEmpty()

                        return@withContext Result.failure(
                            Exception("Error al actualizar nombre: $txt")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Actualizar correo ──────────────────────────────────────
    suspend fun actualizarCorreo(
        accessToken: String,
        userId: String,
        nuevoCorreo: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authBody = JSONObject()
                    .put("email", nuevoCorreo)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val authRequest = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/user")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .put(authBody)
                    .build()

                client.newCall(authRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        val txt = response.body?.string().orEmpty()

                        return@withContext Result.failure(
                            Exception("Error al actualizar correo: $txt")
                        )
                    }
                }

                val perfilBody = JSONObject()
                    .put("correo", nuevoCorreo)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val perfilRequest = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/perfiles?id=eq.$userId")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .patch(perfilBody)
                    .build()

                client.newCall(perfilRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        val txt = response.body?.string().orEmpty()

                        return@withContext Result.failure(
                            Exception("Error al sincronizar correo: $txt")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Actualizar contraseña ──────────────────────────────────
    suspend fun actualizarPassword(
        accessToken: String,
        nuevaPassword: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("password", nuevaPassword)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/user")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .put(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val txt = response.body?.string().orEmpty()

                        return@withContext Result.failure(
                            Exception("Error al cambiar contraseña: $txt")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Crear solicitud real ───────────────────────────────────
    suspend fun crearSolicitud(
        accessToken: String,
        clienteId: String,
        trabajadorId: String,
        servicio: String,
        descripcion: String,
        nombreCliente: String,
        telefonoCliente: String,
        nombreTrabajador: String,
        telefonoTrabajador: String,
        latitudCliente: Double,
        longitudCliente: Double,
        direccionCliente: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("cliente_id", clienteId)
                    .put("trabajador_id", trabajadorId)
                    .put("servicio", servicio)
                    .put("descripcion", descripcion)
                    .put("estado", "Pendiente")
                    .put("latitud_cliente", latitudCliente)
                    .put("longitud_cliente", longitudCliente)
                    .put("direccion_cliente", direccionCliente)
                    .put("nombre_cliente", nombreCliente)
                    .put("telefono_cliente", telefonoCliente)
                    .put("nombre_trabajador", nombreTrabajador)
                    .put("telefono_trabajador", telefonoTrabajador)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/solicitudes")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al crear solicitud: $responseText")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Obtener solicitudes pendientes del trabajador ──────────
    suspend fun obtenerSolicitudesPendientes(
        accessToken: String,
        trabajadorId: String
    ): Result<List<SolicitudServicio>> {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    "$SUPABASE_URL/rest/v1/solicitudes" +
                            "?trabajador_id=eq.$trabajadorId" +
                            "&estado=eq.Pendiente" +
                            "&select=*" +
                            "&order=created_at.desc"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al obtener solicitudes: $responseText")
                        )
                    }

                    val array = JSONArray(responseText)
                    val solicitudes = mutableListOf<SolicitudServicio>()

                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)

                        solicitudes.add(
                            SolicitudServicio(
                                id = item.optString("id"),
                                clienteId = item.optString("cliente_id"),
                                trabajadorId = item.optString("trabajador_id"),
                                servicio = item.optString("servicio"),
                                descripcion = item.optString("descripcion"),
                                estado = item.optString("estado"),
                                latitudCliente = obtenerDoubleNullable(
                                    item,
                                    "latitud_cliente"
                                ),
                                longitudCliente = obtenerDoubleNullable(
                                    item,
                                    "longitud_cliente"
                                ),
                                direccionCliente = item.optString(
                                    "direccion_cliente"
                                ),
                                latitudTrabajador = obtenerDoubleNullable(
                                    item,
                                    "latitud_trabajador"
                                ),
                                longitudTrabajador = obtenerDoubleNullable(
                                    item,
                                    "longitud_trabajador"
                                ),
                                distanciaKm = obtenerDoubleNullable(
                                    item,
                                    "distancia_km"
                                ),
                                duracionMin = obtenerDoubleNullable(
                                    item,
                                    "duracion_min"
                                ),
                                nombreCliente = item.optString(
                                    "nombre_cliente"
                                ),
                                telefonoCliente = item.optString(
                                    "telefono_cliente"
                                ),
                                nombreTrabajador = item.optString(
                                    "nombre_trabajador"
                                ),
                                telefonoTrabajador = item.optString(
                                    "telefono_trabajador"
                                )
                            )
                        )
                    }

                    Result.success(solicitudes)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Aceptar solicitud ──────────────────────────────────────
    suspend fun aceptarSolicitud(
        accessToken: String,
        solicitudId: String,
        trabajadorId: String,
        latitudTrabajador: Double,
        longitudTrabajador: Double
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("estado", "Aceptada")
                    .put("latitud_trabajador", latitudTrabajador)
                    .put("longitud_trabajador", longitudTrabajador)
                    .toString()
                    .toRequestBody(jsonMediaType)

                val url =
                    "$SUPABASE_URL/rest/v1/solicitudes" +
                            "?id=eq.$solicitudId" +
                            "&trabajador_id=eq.$trabajadorId"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .patch(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al aceptar solicitud: $responseText")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ─── Aplicar a una vacante ─────────────────────────────────────
    suspend fun aplicarVacante(
        accessToken: String,
        vacante: Vacante,
        perfil: PerfilUsuario
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = JSONObject()
                    .put("vacante_id", vacante.id)
                    .put("usuario_id", perfil.id)
                    .put("nombre_usuario", perfil.nombre)
                    .put("correo_usuario", perfil.correo)
                    .put("telefono_usuario", perfil.telefono)
                    .put("empresa", vacante.empresa)
                    .put("puesto", vacante.puesto)
                    .put("estado", "Pendiente")
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/postulaciones?on_conflict=vacante_id,usuario_id")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "resolution=ignore-duplicates,return=minimal")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("Error al aplicar a la vacante: $responseText")
                        )
                    }

                    Result.success(Unit)
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun obtenerDoubleNullable(
        json: JSONObject,
        campo: String
    ): Double? {
        return if (!json.has(campo) || json.isNull(campo)) {
            null
        } else {
            json.optDouble(campo)
        }
    }
}




data class SesionSupabase(
    val userId: String,
    val correo: String,
    val accessToken: String,
    val refreshToken: String = ""
)

data class PerfilUsuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val tipoUsuario: String,
    val oficio: String,
    val descripcion: String,
    val disponible: Boolean
)

data class SolicitudServicio(
    val id: String,
    val clienteId: String,
    val trabajadorId: String,
    val servicio: String,
    val descripcion: String,
    val estado: String,
    val latitudCliente: Double?,
    val longitudCliente: Double?,
    val direccionCliente: String,
    val latitudTrabajador: Double?,
    val longitudTrabajador: Double?,
    val distanciaKm: Double?,
    val duracionMin: Double?,
    val nombreCliente: String,
    val telefonoCliente: String,
    val nombreTrabajador: String,
    val telefonoTrabajador: String
)

data class Vacante(
    val id: String,
    val empresa: String,
    val puesto: String,
    val salario: String,
    val horario: String,
    val descripcion: String,
    val requisitos: String,
    val beneficios: String,
    val telefono: String,
    val direccion: String,
    val latitud: Double?,
    val longitud: Double?,
    val disponible: Boolean
)