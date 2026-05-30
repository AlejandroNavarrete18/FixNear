package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Reporte
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(reporte: Reporte): Long

    @Update
    suspend fun actualizar(reporte: Reporte)

    @Query("SELECT * FROM reporte ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<Reporte>>

    @Query("SELECT * FROM reporte WHERE reportado_id = :usuarioId ORDER BY fecha DESC")
    fun obtenerPorReportado(usuarioId: Int): Flow<List<Reporte>>
}
