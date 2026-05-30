package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(servicio: Servicio): Long

    @Update
    suspend fun actualizar(servicio: Servicio)

    @Delete
    suspend fun eliminar(servicio: Servicio)

    @Query("SELECT * FROM servicio WHERE activo = 1 ORDER BY fecha_creacion DESC")
    fun obtenerActivos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicio WHERE trabajador_id = :trabajadorId")
    fun obtenerPorTrabajador(trabajadorId: Int): Flow<List<Servicio>>

    @Query("SELECT * FROM servicio WHERE categoria_id = :categoriaId AND activo = 1")
    fun obtenerPorCategoria(categoriaId: Int): Flow<List<Servicio>>
}
