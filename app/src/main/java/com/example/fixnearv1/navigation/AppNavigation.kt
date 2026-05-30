package com.example.fixnearv1.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fixnearv1.iuu.screens.*
import com.example.fixnearv1.modelo.EmpleoDemo
import com.example.fixnearv1.modelo.entity.Usuario
import com.example.fixnearv1.modelo.repository.MovimientoRepository
import com.example.fixnearv1.modelo.repository.ProductoRepository
import com.example.fixnearv1.modelo.repository.UsuarioRepository
import com.example.fixnearv1.viewmodel.Factory.LoginViewModelFactory
import com.example.fixnearv1.viewmodel.Factory.MovimientoViewModelFactory
import com.example.fixnearv1.viewmodel.Factory.ProductoViewModelFactory
import com.example.fixnearv1.viewmodel.LoginViewModel
import com.example.fixnearv1.viewmodel.MovimientoViewModel
import com.example.fixnearv1.viewmodel.ProductoViewModel

@androidx.camera.core.ExperimentalGetImage
@Composable
fun AppNavigation(
    usuarioRepository: UsuarioRepository,
    productoRepository: ProductoRepository,
    movimientoRepository: MovimientoRepository
) {
    var pantallaActual by remember { mutableStateOf("splash") }
    var empleoSeleccionado by remember { mutableStateOf<EmpleoDemo?>(null) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(usuarioRepository)
    )
    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(productoRepository)
    )
    val movimientoViewModel: MovimientoViewModel = viewModel(
        factory = MovimientoViewModelFactory(movimientoRepository)
    )

    when (pantallaActual) {

        "splash" -> SplashScreen(
            onNavigateToWelcome = { pantallaActual = "welcome" }
        )

        "welcome" -> WelcomeScreen(
            onIniciarSesion = { pantallaActual = "login" },
            onCrearCuenta = { pantallaActual = "registro" }
        )

        "login" -> LoginScreen(
            viewModel = loginViewModel,
            onLoginExitoso = { usuario ->
                usuarioActual = usuario
                pantallaActual = "menu"
            },
            onCrearCuenta = { pantallaActual = "registro" }
        )

        "registro" -> RegisterScreen(
            viewModel = loginViewModel,
            onCuentaCreada = { pantallaActual = "verificacion" },
            onRegresar = { pantallaActual = "login" }
        )

        "verificacion" -> VerifyEmailScreen(
            onVerificado = { pantallaActual = "menu" },
            onRegresar = { pantallaActual = "registro" }
        )

        "menu" -> MenuScreen(
            usuario = usuarioActual,
            onServiciosClick = { pantallaActual = "servicios" },
            onPerfilClick = { pantallaActual = "perfil" },
            onTrabajadoresClick = { pantallaActual = "trabajadorHome" },
            onEmpleosClick = { pantallaActual = "empleos" },
            onQrClick = { pantallaActual = "qr" }
        )

        "servicios" -> ServiciosScreen(
            onRegresar = { pantallaActual = "menu" },
            onVerPerfilTrabajador = { pantallaActual = "perfilTrabajador" }
        )

        "perfilTrabajador" -> PerfilTrabajadorScreen(
            onRegresar = { pantallaActual = "servicios" }
        )

        "trabajadorHome" -> TrabajadorHomeScreen(
            onRegresar = { pantallaActual = "menu" }
        )

        "empleos" -> EmpleosScreen(
            onRegresar = { pantallaActual = "menu" },
            onVerDetalle = { empleo ->
                empleoSeleccionado = empleo
                pantallaActual = "detalleEmpleo"
            }
        )

        "detalleEmpleo" -> {
            if (empleoSeleccionado != null) {
                DetalleVacanteScreen(
                    empleo = empleoSeleccionado!!,
                    onRegresar = {
                        pantallaActual = "empleos"
                        empleoSeleccionado = null
                    }
                )
            }
        }

        "qr" -> QrScannerScreen(
            onRegresar = { pantallaActual = "menu" }
        )

        "perfil" -> PerfilScreen(
            usuario = usuarioActual,
            onRegresar = { pantallaActual = "menu" },
            onCerrarSesion = {
                loginViewModel.cerrarSesion()
                usuarioActual = null
                pantallaActual = "login"
            }
        )
    }
}
