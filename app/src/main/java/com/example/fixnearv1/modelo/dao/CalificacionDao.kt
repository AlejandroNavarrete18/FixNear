package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Calificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface CalificacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(calificacion: Calificacion): Long

    @Delete
    suspend fun eliminar(calificacion: Calificacion)

    @Query("SELECT * FROM calificacion WHERE calificado_id = :usuarioId ORDER BY fecha DESC")
    fun obtenerPorCalificado(usuarioId: Int): Flow<List<Calificacion>>

    @Query("SELECT AVG(puntuacion) FROM calificacion WHERE calificado_id = :usuarioId")
    suspend fun promedioCalificacion(usuarioId: Int): Double?
}
