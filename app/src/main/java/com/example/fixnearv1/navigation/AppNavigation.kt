package com.example.fixnearv1.navigation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.fixnearv1.MainActivity
import com.example.fixnearv1.iuu.screens.*
import com.example.fixnearv1.modelo.EmpleoDemo
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SesionSupabase
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun AppNavigation() {

    var pantallaActual by remember { mutableStateOf("splash") }
    var empleoSeleccionado by remember { mutableStateOf<EmpleoDemo?>(null) }
    var correoRecuperacion by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Procesar deep link de OAuth (Google) al volver del navegador
    LaunchedEffect(Unit) {
        val intent = MainActivity.pendingIntent ?: return@LaunchedEffect
        val data: Uri = intent.data ?: return@LaunchedEffect

        // Supabase redirige con: fixnear://auth/callback#access_token=...&refresh_token=...
        if (data.scheme == "fixnear" && data.host == "auth") {
            val fragment = data.fragment ?: return@LaunchedEffect
            val params = fragment.split("&").associate {
                val (k, v) = it.split("=", limit = 2)
                k to v
            }
            val accessToken = params["access_token"].orEmpty()
            val refreshToken = params["refresh_token"].orEmpty()
            val userId = params["user_id"]
                ?: run {
                    // Decodificar user id del JWT si no viene explícito
                    try {
                        val payload = accessToken.split(".")[1]
                        val decoded = android.util.Base64.decode(
                            payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '='),
                            android.util.Base64.URL_SAFE
                        )
                        val json = org.json.JSONObject(String(decoded))
                        json.optString("sub")
                    } catch (e: Exception) { "" }
                }
            val correo = try {
                val payload = accessToken.split(".")[1]
                val decoded = android.util.Base64.decode(
                    payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '='),
                    android.util.Base64.URL_SAFE
                )
                val json = org.json.JSONObject(String(decoded))
                json.optString("email")
            } catch (e: Exception) { "" }

            if (accessToken.isNotBlank() && userId.isNotBlank()) {
                SesionLocal.guardarSesion(
                    context = context,
                    sesion = SesionSupabase(
                        userId = userId,
                        correo = correo,
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    ),
                    recordar = true // Las sesiones de Google se recuerdan siempre
                )
                MainActivity.pendingIntent = null
                pantallaActual = "menu"
            }
        }
    }

    when (pantallaActual) {

        "splash" -> SplashScreen(
            onNavigateToLogin = { pantallaActual = "login" }
        )

        "login" -> LoginScreen(
            onLoginExitoso = { pantallaActual = "menu" },
            onCrearCuenta = { pantallaActual = "registro" },
            onOlvidoPassword = { pantallaActual = "recuperar_password" }
        )

        "recuperar_password" -> ForgotPasswordScreen(
            onRegresar = { pantallaActual = "login" },
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
            onRegresar = { pantallaActual = "menu" },
            onCerrarSesion = { pantallaActual = "login" }
        )
    }
}
