package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: Categoria): Long

    @Update
    suspend fun actualizar(categoria: Categoria)

    @Delete
    suspend fun eliminar(categoria: Categoria)

    @Query("SELECT * FROM categoria WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerActivas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categoria ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categoria WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Categoria?
}
