package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Movimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(movimiento: Movimiento): Long

    @Query("SELECT * FROM movimiento WHERE usuario_id = :usuarioId ORDER BY fecha DESC")
    fun obtenerPorUsuario(usuarioId: Int): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimiento ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<Movimiento>>

    @Delete
    suspend fun eliminar(movimiento: Movimiento)
}
