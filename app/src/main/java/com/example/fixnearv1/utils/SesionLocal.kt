package com.example.fixnearv1.utils

import android.content.Context

object SesionLocal {

    private const val PREFS = "fixnear_sesion"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_CORREO = "correo"
    private const val KEY_RECORDAR = "recordar_sesion"

    // ─── Guardar sesión completa ─────────────────────────────────────────────
    fun guardarSesion(
        context: Context,
        sesion: SesionSupabase,
        recordar: Boolean = false
    ) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_ID, sesion.userId)
            .putString(KEY_ACCESS_TOKEN, sesion.accessToken)
            .putString(KEY_REFRESH_TOKEN, sesion.refreshToken)
            .putString(KEY_CORREO, sesion.correo)
            .putBoolean(KEY_RECORDAR, recordar)
            .apply()
    }

    // ─── Verificar si hay sesión guardada para auto-login ────────────────────
    fun haySesionGuardada(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val recordar = prefs.getBoolean(KEY_RECORDAR, false)
        val userId = prefs.getString(KEY_USER_ID, "").orEmpty()
        val token = prefs.getString(KEY_ACCESS_TOKEN, "").orEmpty()
        return recordar && userId.isNotBlank() && token.isNotBlank()
    }

    // ─── Obtener sesión guardada (para refrescar token si expiró) ────────────
    fun obtenerSesionGuardada(context: Context): SesionSupabase? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val userId = prefs.getString(KEY_USER_ID, "").orEmpty()
        val token = prefs.getString(KEY_ACCESS_TOKEN, "").orEmpty()
        val refresh = prefs.getString(KEY_REFRESH_TOKEN, "").orEmpty()
        val correo = prefs.getString(KEY_CORREO, "").orEmpty()
        if (userId.isBlank()) return null
        return SesionSupabase(
            userId = userId,
            correo = correo,
            accessToken = token,
            refreshToken = refresh
        )
    }

    fun obtenerUserId(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_USER_ID, "").orEmpty()
    }

    fun obtenerAccessToken(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ACCESS_TOKEN, "").orEmpty()
    }

    fun obtenerRefreshToken(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_REFRESH_TOKEN, "").orEmpty()
    }

    // ─── Actualizar access token después de refrescar ────────────────────────
    fun actualizarTokens(
        context: Context,
        accessToken: String,
        refreshToken: String
    ) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    // ─── Cerrar sesión (borra todo) ──────────────────────────────────────────
    fun cerrarSesion(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
