package com.example.fixnearv1.modelo.repository

import com.example.fixnearv1.modelo.dao.ProductoDao
import com.example.fixnearv1.modelo.entity.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    val producto: Flow<List<Producto>> = productoDao.obtenerTodos()

    suspend fun insertar(producto: Producto) {
        productoDao.insertar(producto)
    }

    suspend fun actualizar(producto: Producto) {
        productoDao.actualizar(producto)
    }

    suspend fun eliminar(producto: Producto) {
        productoDao.eliminar(producto)
    }
}
