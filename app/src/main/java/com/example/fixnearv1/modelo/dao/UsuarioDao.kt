package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Delete
    suspend fun eliminar(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun obtenerPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuario WHERE email = :email AND password_hash = :passwordHash LIMIT 1")
    suspend fun login(email: String, passwordHash: String): Usuario?

    @Query("SELECT * FROM usuario WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuario WHERE rol = 'trabajador' AND activo = 1")
    fun obtenerTrabajadores(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario")
    fun obtenerTodos(): Flow<List<Usuario>>

    @Query("UPDATE usuario SET disponible = :disponible WHERE id = :id")
    suspend fun actualizarDisponibilidad(id: Int, disponible: Int)
}
