package com.example.fixnearv1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixnearv1.modelo.entity.Usuario
import com.example.fixnearv1.modelo.repository.UsuarioRepository
import com.example.fixnearv1.utils.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Cargando : LoginState()
    data class Exitoso(val usuario: Usuario) : LoginState()
    data class Error(val mensaje: String) : LoginState()
}

sealed class RegistroState {
    object Idle : RegistroState()
    object Cargando : RegistroState()
    object Exitoso : RegistroState()
    data class Error(val mensaje: String) : RegistroState()
}

class LoginViewModel(
    private val repositorio: UsuarioRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registroState = MutableStateFlow<RegistroState>(RegistroState.Idle)
    val registroState: StateFlow<RegistroState> = _registroState

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Completa todos los campos")
            return
        }
        if (!Validaciones.esEmailValido(email)) {
            _loginState.value = LoginState.Error("Correo electrónico no válido")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Cargando
            val usuario = repositorio.login(email.trim(), password)
            if (usuario != null) {
                _usuarioActual.value = usuario
                _loginState.value = LoginState.Exitoso(usuario)
            } else {
                _loginState.value = LoginState.Error("Correo o contraseña incorrectos")
            }
        }
    }

    fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        confirmar: String,
        telefono: String,
        rol: String = "cliente"
    ) {
        when {
            nombre.isBlank() || apellido.isBlank() || email.isBlank() ||
                    password.isBlank() || confirmar.isBlank() || telefono.isBlank() -> {
                _registroState.value = RegistroState.Error("Completa todos los campos")
                return
            }
            !Validaciones.esEmailValido(email) -> {
                _registroState.value = RegistroState.Error("Correo electrónico no válido")
                return
            }
            !Validaciones.esTelefonoValido(telefono) -> {
                _registroState.value = RegistroState.Error("Teléfono no válido")
                return
            }
            password != confirmar -> {
                _registroState.value = RegistroState.Error("Las contraseñas no coinciden")
                return
            }
            !Validaciones.esPasswordValido(password) -> {
                _registroState.value = RegistroState.Error("La contraseña debe tener al menos 6 caracteres")
                return
            }
        }
        viewModelScope.launch {
            _registroState.value = RegistroState.Cargando
            val existe = repositorio.existeEmail(email.trim())
            if (existe) {
                _registroState.value = RegistroState.Error("Este correo ya está registrado")
                return@launch
            }
            repositorio.registrar(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                email = email.trim(),
                password = password,
                telefono = telefono.trim(),
                rol = rol.lowercase()
            )
            _registroState.value = RegistroState.Exitoso
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        _loginState.value = LoginState.Idle
        _registroState.value = RegistroState.Idle
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegistroState() {
        _registroState.value = RegistroState.Idle
    }
}
