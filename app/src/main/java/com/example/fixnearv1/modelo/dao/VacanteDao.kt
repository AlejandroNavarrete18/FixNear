package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Vacante
import kotlinx.coroutines.flow.Flow

@Dao
interface VacanteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(vacante: Vacante): Long

    @Update
    suspend fun actualizar(vacante: Vacante)

    @Delete
    suspend fun eliminar(vacante: Vacante)

    @Query("SELECT * FROM vacante WHERE estado = 'activa' ORDER BY fecha_publicacion DESC")
    fun obtenerActivas(): Flow<List<Vacante>>

    @Query("SELECT * FROM vacante WHERE empleador_id = :empleadorId")
    fun obtenerPorEmpleador(empleadorId: Int): Flow<List<Vacante>>

    @Query("SELECT * FROM vacante WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Vacante?
}
