package com.example.fixnearv1.modelo.repository

import com.example.fixnearv1.modelo.dao.MovimientoDao
import com.example.fixnearv1.modelo.entity.Movimiento
import kotlinx.coroutines.flow.Flow

class MovimientoRepository(private val movimientoDao: MovimientoDao) {

    fun obtenerPorUsuario(usuarioId: Int): Flow<List<Movimiento>> =
        movimientoDao.obtenerPorUsuario(usuarioId)

    val todos: Flow<List<Movimiento>> = movimientoDao.obtenerTodos()

    suspend fun registrar(
        usuarioId: Int,
        tipo: String,
        monto: Double,
        descripcion: String? = null,
        referenciaTipo: String = "otro",
        referenciaId: Int? = null
    ) {
        val movimiento = Movimiento(
            usuarioId = usuarioId,
            tipo = tipo,
            referenciaTipo = referenciaTipo,
            referenciaId = referenciaId,
            monto = monto,
            descripcion = descripcion,
            fecha = System.currentTimeMillis().toString()
        )
        movimientoDao.insertar(movimiento)
    }
}
