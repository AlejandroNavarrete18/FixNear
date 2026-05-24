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

    @Query("""
        SELECT * FROM productos
        ORDER BY nombre ASC
    """)
    fun obtenerTodos(): Flow<List<Producto>>
}