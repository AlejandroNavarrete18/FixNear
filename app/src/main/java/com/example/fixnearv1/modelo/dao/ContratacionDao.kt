package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Contratacion
import kotlinx.coroutines.flow.Flow

@Dao
interface ContratacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(contratacion: Contratacion): Long

    @Update
    suspend fun actualizar(contratacion: Contratacion)

    @Delete
    suspend fun eliminar(contratacion: Contratacion)

    @Query("SELECT * FROM contratacion WHERE cliente_id = :clienteId ORDER BY fecha_creacion DESC")
    fun obtenerPorCliente(clienteId: Int): Flow<List<Contratacion>>

    @Query("SELECT * FROM contratacion WHERE trabajador_id = :trabajadorId ORDER BY fecha_creacion DESC")
    fun obtenerPorTrabajador(trabajadorId: Int): Flow<List<Contratacion>>

    @Query("SELECT * FROM contratacion WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Contratacion?
}
