package com.example.fixnearv1.navigation

import androidx.compose.runtime.*
import com.example.fixnearv1.iuu.screens.*
import com.example.fixnearv1.modelo.EmpleoDemo // Muy importante este import

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun AppNavigation() {

    // 1. CAMBIO AQUÍ: Ahora la app arranca en "splash" en lugar de "welcome"
    var pantallaActual by remember { mutableStateOf("splash") }

    // Aquí guardamos la vacante a la que el usuario le dio clic
    var empleoSeleccionado by remember { mutableStateOf<EmpleoDemo?>(null) }

    when (pantallaActual) {

        // 2. NUEVO: Agregamos la ruta del Splash Screen
        "splash" -> SplashScreen(
            onNavigateToWelcome = { pantallaActual = "welcome" } // Después de 2.5s cambia a welcome
        )

        "welcome" -> WelcomeScreen(
            onIniciarSesion = { pantallaActual = "login" },
            onCrearCuenta = { pantallaActual = "registro" }
        )
        "login" -> LoginScreen(
            onLoginExitoso = { pantallaActual = "menu" },
            onCrearCuenta = { pantallaActual = "registro" }
        )
        "registro" -> RegisterScreen(
            onCuentaCreada = { pantallaActual = "verificacion" },
            onRegresar = { pantallaActual = "login" }
        )
        "verificacion" -> VerifyEmailScreen(
            onVerificado = { pantallaActual = "menu" },
            onRegresar = { pantallaActual = "registro" }
        )
        "menu" -> MenuScreen(
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
            // Cuando en EmpleosScreen le dan a "Ver vacante", pasa esto:
            onVerDetalle = { empleo ->
                empleoSeleccionado = empleo // Guardamos el empleo
                pantallaActual = "detalleEmpleo" // Cambiamos de pantalla
            }
        )

        // La pantalla que acabamos de crear
        "detalleEmpleo" -> {
            // Revisamos si guardamos el empleo correctamente
            if (empleoSeleccionado != null) {
                DetalleVacanteScreen(
                    empleo = empleoSeleccionado!!, // Se lo mandamos a la pantalla
                    onRegresar = {
                        pantallaActual = "empleos" // Volvemos a la lista
                        empleoSeleccionado = null // Limpiamos la memoria
                    }
                )
            }
        }

        "qr" -> QrScannerScreen(
            onRegresar = { pantallaActual = "menu" }
        )
        "perfil" -> PerfilScreen(
            onRegresar = { pantallaActual = "menu" },
            onCerrarSesion = { pantallaActual = "login" }
        )
    }
}