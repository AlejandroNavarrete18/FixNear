package com.example.fixnearv1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixnearv1.modelo.entity.Movimiento
import com.example.fixnearv1.modelo.repository.MovimientoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MovimientoViewModel(
    private val repositorio: MovimientoRepository
) : ViewModel() {

    val todos: Flow<List<Movimiento>> = repositorio.todos

    fun obtenerPorUsuario(usuarioId: Int): Flow<List<Movimiento>> =
        repositorio.obtenerPorUsuario(usuarioId)

    fun registrarIngreso(usuarioId: Int, monto: Double, descripcion: String? = null) {
        viewModelScope.launch {
            repositorio.registrar(
                usuarioId = usuarioId,
                tipo = "ingreso",
                monto = monto,
                descripcion = descripcion
            )
        }
    }

    fun registrarEgreso(usuarioId: Int, monto: Double, descripcion: String? = null) {
        viewModelScope.launch {
            repositorio.registrar(
                usuarioId = usuarioId,
                tipo = "egreso",
                monto = monto,
                descripcion = descripcion
            )
        }
    }
}
