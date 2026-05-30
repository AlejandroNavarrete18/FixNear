package com.example.fixnearv1.utils

object Validaciones {

    fun esEmailValido(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun esPasswordValido(password: String): Boolean {
        return password.length >= 6
    }

    fun esTelefonoValido(telefono: String): Boolean {
        return telefono.length >= 10 && telefono.all { it.isDigit() || it == '+' || it == '-' }
    }
}
