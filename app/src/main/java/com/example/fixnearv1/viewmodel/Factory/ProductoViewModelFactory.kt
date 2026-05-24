package com.example.fixnearv1.viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fixnearv1.modelo.repository.ProductoRepository
import com.example.fixnearv1.viewmodel.ProductoViewModel

class ProductoViewModelFactory(
    private val repositorio: ProductoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        @Suppress("UNCHECKED_CAST")
        return ProductoViewModel(repositorio) as T
    }
}