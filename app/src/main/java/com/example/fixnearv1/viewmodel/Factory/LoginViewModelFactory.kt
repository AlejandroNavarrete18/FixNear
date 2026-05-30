package com.example.fixnearv1.viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fixnearv1.modelo.repository.UsuarioRepository
import com.example.fixnearv1.viewmodel.LoginViewModel

class LoginViewModelFactory(
    private val repositorio: UsuarioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(repositorio) as T
    }
}
