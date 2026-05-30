package com.example.fixnearv1.modelo.repository

import com.example.fixnearv1.modelo.dao.UsuarioDao
import com.example.fixnearv1.modelo.entity.Usuario
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    val trabajadores: Flow<List<Usuario>> = usuarioDao.obtenerTrabajadores()
    val todos: Flow<List<Usuario>> = usuarioDao.obtenerTodos()

    suspend fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        telefono: String,
        rol: String = "cliente"
    ): Long {
        val passwordHash = hashPassword(password)
        val usuario = Usuario(
            nombre = nombre,
            apellido = apellido,
            email = email,
            passwordHash = passwordHash,
            telefono = telefono,
            rol = rol,
            fechaRegistro = System.currentTimeMillis().toString()
        )
        return usuarioDao.insertar(usuario)
    }

    suspend fun login(email: String, password: String): Usuario? {
        val passwordHash = hashPassword(password)
        return usuarioDao.login(email, passwordHash)
    }

    suspend fun existeEmail(email: String): Boolean {
        return usuarioDao.obtenerPorEmail(email) != null
    }

    suspend fun obtenerPorId(id: Int): Usuario? {
        return usuarioDao.obtenerPorId(id)
    }

    suspend fun actualizarDisponibilidad(id: Int, disponible: Boolean) {
        usuarioDao.actualizarDisponibilidad(id, if (disponible) 1 else 0)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
