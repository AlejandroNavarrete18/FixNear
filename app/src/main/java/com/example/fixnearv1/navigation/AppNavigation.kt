package com.example.fixnearv1.navigation

import androidx.compose.runtime.*
import com.example.fixnearv1.iuu.screens.*

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun AppNavigation() {

    var pantallaActual by remember {
        mutableStateOf("welcome")
    }

    when (pantallaActual) {

        "welcome" -> {
            WelcomeScreen(
                onIniciarSesion = {
                    pantallaActual = "login"
                },
                onCrearCuenta = {
                    pantallaActual = "registro"
                }
            )
        }

        "login" -> {
            LoginScreen(
                onLoginExitoso = {
                    pantallaActual = "menu"
                },
                onCrearCuenta = {
                    pantallaActual = "registro"
                }
            )
        }

        "registro" -> {
            RegisterScreen(
                onCuentaCreada = {
                    pantallaActual = "verificacion"
                },
                onRegresar = {
                    pantallaActual = "login"
                }
            )
        }

        "verificacion" -> {
            VerifyEmailScreen(
                onVerificado = {
                    pantallaActual = "menu"
                },
                onRegresar = {
                    pantallaActual = "registro"
                }
            )
        }

        "menu" -> {
            MenuScreen(
                onServiciosClick = {
                    pantallaActual = "servicios"
                },
                onPerfilClick = {
                    pantallaActual = "perfil"
                },
                onTrabajadoresClick = {
                    pantallaActual = "trabajadorHome"
                },
                onEmpleosClick = {
                    pantallaActual = "empleos"
                },
                onQrClick = {
                    pantallaActual = "qr"
                }
            )
        }

        "servicios" -> {
            ServiciosScreen(
                onRegresar = {
                    pantallaActual = "menu"
                },
                onVerPerfilTrabajador = {
                    pantallaActual = "perfilTrabajador"
                }
            )
        }

        "perfilTrabajador" -> {
            PerfilTrabajadorScreen(
                onRegresar = {
                    pantallaActual = "servicios"
                }
            )
        }

        "trabajadorHome" -> {
            TrabajadorHomeScreen(
                onRegresar = {
                    pantallaActual = "menu"
                }
            )
        }

        "empleos" -> {
            EmpleosScreen(
                onRegresar = {
                    pantallaActual = "menu"
                }
            )
        }

        "qr" -> {
            QrScannerScreen(
                onRegresar = {
                    pantallaActual = "menu"
                }
            )
        }

        "perfil" -> {
            PerfilScreen(
                onRegresar = {
                    pantallaActual = "menu"
                },
                onCerrarSesion = {
                    pantallaActual = "login"
                }
            )
        }
    }
}