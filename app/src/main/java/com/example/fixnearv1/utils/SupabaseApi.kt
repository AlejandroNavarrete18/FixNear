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

    // ─── URL para OAuth con Google (abre en navegador externo) ───────────────
    fun obtenerUrlGoogleOAuth(): String {
        return "$SUPABASE_URL/auth/v1/authorize" +
            "?provider=google" +
            "&redirect_to=fixnear://auth/callback"
    }

    // ─── Intercambiar código OAuth por sesión ────────────────────────────────
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

    // ─── Registrar usuario ───────────────────────────────────────────────────
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
                    var accessToken = session?.optString("access_token").orEmpty()
                    val refreshToken = session?.optString("refresh_token").orEmpty()

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

    // ─── Iniciar sesión ──────────────────────────────────────────────────────
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

    // ─── Refrescar token con refreshToken ────────────────────────────────────
    suspend fun refrescarSesion(refreshToken: String): Result<SesionSupabase> {
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

    // ─── Crear perfil ────────────────────────────────────────────────────────
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
                    .addHeader("Prefer", "resolution=merge-duplicates,return=minimal")
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

    // ─── Obtener perfil ──────────────────────────────────────────────────────
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

    // ─── Actualizar nombre en tabla perfiles ─────────────────────────────────
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

    // ─── Actualizar correo en Auth y tabla perfiles ──────────────────────────
    suspend fun actualizarCorreo(
        accessToken: String,
        userId: String,
        nuevoCorreo: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Actualizar en Supabase Auth
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

                // 2. Actualizar en tabla perfiles
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

    // ─── Actualizar contraseña ───────────────────────────────────────────────
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
