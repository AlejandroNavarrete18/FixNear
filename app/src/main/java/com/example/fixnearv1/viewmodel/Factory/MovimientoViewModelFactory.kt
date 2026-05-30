package com.example.fixnearv1.viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fixnearv1.modelo.repository.MovimientoRepository
import com.example.fixnearv1.viewmodel.MovimientoViewModel

class MovimientoViewModelFactory(
    private val repositorio: MovimientoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MovimientoViewModel(repositorio) as T
    }
}
