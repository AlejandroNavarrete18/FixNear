package com.example.fixnearv1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixnearv1.modelo.entity.Producto
import com.example.fixnearv1.modelo.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repositorio: ProductoRepository
) : ViewModel() {

    val productos: Flow<List<Producto>> =
        repositorio.producto

    fun agregarProducto(
        nombre: String,
        codigoQR: String,
        cantidad: String,
        precio: String,
        categoria: String
    ) {

        val cantidadInt = cantidad.toIntOrNull() ?: 0
        val precioDouble = precio.toDoubleOrNull() ?: 0.0

        val producto = Producto(
            nombre = nombre,
            codigoQR = codigoQR,
            cantidad = cantidadInt,
            precio = precioDouble,
            categoria = categoria
        )

        viewModelScope.launch {
            repositorio.insertar(producto)
        }
    }

    fun eliminarProducto(producto: Producto) {

        viewModelScope.launch {
            repositorio.eliminar(producto)
        }
    }
}