package com.example.fixnearv1.modelo.dao

import androidx.room.*
import com.example.fixnearv1.modelo.entity.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("SELECT * FROM producto ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM producto WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerActivos(): Flow<List<Producto>>

    @Query("SELECT * FROM producto WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Producto?
}
