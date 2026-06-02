package com.example.fixnearv1.navigation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.fixnearv1.MainActivity
import com.example.fixnearv1.iuu.screens.*
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SesionSupabase
import com.example.fixnearv1.utils.Vacante

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun AppNavigation() {

    var pantallaActual by remember {
        mutableStateOf("splash")
    }

    var vacanteSeleccionada by remember {
        mutableStateOf<Vacante?>(null)
    }

    var correoRecuperacion by remember {
        mutableStateOf("")
    }

    var trabajadorSeleccionadoId by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    // Procesar deep link de OAuth con Google
    LaunchedEffect(Unit) {
        val intent = MainActivity.pendingIntent ?: return@LaunchedEffect
        val data: Uri = intent.data ?: return@LaunchedEffect

        if (data.scheme == "fixnear" && data.host == "auth") {
            val fragment = data.fragment ?: return@LaunchedEffect

            val params = fragment.split("&").associate {
                val partes = it.split("=", limit = 2)

                if (partes.size == 2) {
                    partes[0] to partes[1]
                } else {
                    "" to ""
                }
            }

            val accessToken = params["access_token"].orEmpty()
            val refreshToken = params["refresh_token"].orEmpty()

            val userId = params["user_id"] ?: run {
                try {
                    val payload = accessToken.split(".")[1]

                    val decoded = android.util.Base64.decode(
                        payload.padEnd(
                            payload.length + (4 - payload.length % 4) % 4,
                            '='
                        ),
                        android.util.Base64.URL_SAFE
                    )

                    val json = org.json.JSONObject(String(decoded))
                    json.optString("sub")
                } catch (e: Exception) {
                    ""
                }
            }

            val correo = try {
                val payload = accessToken.split(".")[1]

                val decoded = android.util.Base64.decode(
                    payload.padEnd(
                        payload.length + (4 - payload.length % 4) % 4,
                        '='
                    ),
                    android.util.Base64.URL_SAFE
                )

                val json = org.json.JSONObject(String(decoded))
                json.optString("email")
            } catch (e: Exception) {
                ""
            }

            if (accessToken.isNotBlank() && userId.isNotBlank()) {
                SesionLocal.guardarSesion(
                    context = context,
                    sesion = SesionSupabase(
                        userId = userId,
                        correo = correo,
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    ),
                    recordar = true
                )

                MainActivity.pendingIntent = null
                pantallaActual = "menu"
            }
        }
    }

    when (pantallaActual) {

        "splash" -> SplashScreen(
            onNavigateToLogin = {
                pantallaActual = "login"
            }
        )

        "login" -> LoginScreen(
            onLoginExitoso = {
                pantallaActual = "menu"
            },
            onCrearCuenta = {
                pantallaActual = "registro"
            },
            onOlvidoPassword = {
                pantallaActual = "recuperar_password"
            }
        )

        "recuperar_password" -> ForgotPasswordScreen(
            onRegresar = {
                pantallaActual = "login"
            },
            onEnviarInstrucciones = { correoIngresado ->
                correoRecuperacion = correoIngresado
                pantallaActual = "success"
            }
        )

        "success" -> SuccessScreen(
            emailUsuario = correoRecuperacion,
            onNavigateToLogin = {
                pantallaActual = "login"
                correoRecuperacion = ""
            },
            onBackClick = {
                pantallaActual = "recuperar_password"
                correoRecuperacion = ""
            }
        )

        "registro" -> RegisterScreen(
            onCuentaCreada = {
                pantallaActual = "verificacion"
            },
            onRegresar = {
                pantallaActual = "login"
            }
        )

        "verificacion" -> VerifyEmailScreen(
            onVerificado = {
                pantallaActual = "menu"
            },
            onRegresar = {
                pantallaActual = "registro"
            }
        )

        "menu" -> MenuScreen(
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

        "servicios" -> ServiciosScreen(
            onRegresar = {
                pantallaActual = "menu"
            },
            onVerPerfilTrabajador = { trabajadorId ->
                trabajadorSeleccionadoId = trabajadorId
                pantallaActual = "perfilTrabajador"
            }
        )

        "perfilTrabajador" -> {
            if (trabajadorSeleccionadoId.isNotBlank()) {
                PerfilTrabajadorScreen(
                    trabajadorId = trabajadorSeleccionadoId,
                    onRegresar = {
                        pantallaActual = "servicios"
                    }
                )
            } else {
                pantallaActual = "servicios"
            }
        }

        "trabajadorHome" -> TrabajadorHomeScreen(
            onRegresar = {
                pantallaActual = "menu"
            }
        )

        "empleos" -> EmpleosScreen(
            onRegresar = {
                pantallaActual = "menu"
            },
            onVerDetalle = { vacante ->
                vacanteSeleccionada = vacante
                pantallaActual = "detalleEmpleo"
            }
        )

        "detalleEmpleo" -> {
            if (vacanteSeleccionada != null) {
                DetalleVacanteScreen(
                    vacante = vacanteSeleccionada!!,
                    onRegresar = {
                        pantallaActual = "empleos"
                        vacanteSeleccionada = null
                    }
                )
            } else {
                pantallaActual = "empleos"
            }
        }

        "qr" -> QrScannerScreen(
            onRegresar = {
                pantallaActual = "menu"
            }
        )

        "perfil" -> PerfilScreen(
            onRegresar = {
                pantallaActual = "menu"
            },
            onCerrarSesion = {
                pantallaActual = "login"
            }
        )
    }
}