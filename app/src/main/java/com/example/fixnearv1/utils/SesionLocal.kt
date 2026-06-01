package com.example.fixnearv1.utils

import android.content.Context

object SesionLocal {

    private const val PREFS = "fixnear_sesion"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_CORREO = "correo"

    fun guardarSesion(
        context: Context,
        sesion: SesionSupabase
    ) {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putString(KEY_USER_ID, sesion.userId)
            .putString(KEY_ACCESS_TOKEN, sesion.accessToken)
            .putString(KEY_CORREO, sesion.correo)
            .apply()
    }

    fun obtenerUserId(context: Context): String {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        return prefs.getString(KEY_USER_ID, "").orEmpty()
    }

    fun obtenerAccessToken(context: Context): String {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        return prefs.getString(KEY_ACCESS_TOKEN, "").orEmpty()
    }

    fun cerrarSesion(context: Context) {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        prefs.edit().clear().apply()
    }
}
