package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Postulacion
import kotlinx.coroutines.flow.Flow

@Dao
interface PostulacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(postulacion: Postulacion): Long

    @Update
    suspend fun actualizar(postulacion: Postulacion)

    @Delete
    suspend fun eliminar(postulacion: Postulacion)

    @Query("SELECT * FROM postulacion WHERE trabajador_id = :trabajadorId ORDER BY fecha_postulacion DESC")
    fun obtenerPorTrabajador(trabajadorId: Int): Flow<List<Postulacion>>

    @Query("SELECT * FROM postulacion WHERE vacante_id = :vacanteId ORDER BY fecha_postulacion DESC")
    fun obtenerPorVacante(vacanteId: Int): Flow<List<Postulacion>>

    @Query("SELECT * FROM postulacion WHERE vacante_id = :vacanteId AND trabajador_id = :trabajadorId LIMIT 1")
    suspend fun obtenerPostulacion(vacanteId: Int, trabajadorId: Int): Postulacion?
}
