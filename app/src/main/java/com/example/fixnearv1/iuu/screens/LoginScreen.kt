package com.example.fixnearv1.iuu.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.R
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SupabaseApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onCrearCuenta: () -> Unit,
    onOlvidoPassword: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var recordarSesion by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val backgroundColor = Color(0xFF0B0F19)
    val inputBackgroundColor = Color(0xFF131826)
    val primaryPurple = Color(0xFF8A2BE2)
    val lightPurple = Color(0xFFB388FF)
    val textGray = Color(0xFFA0AABF)

    // Auto-login si el usuario marcó "Recordar mi cuenta"
    LaunchedEffect(Unit) {
        if (SesionLocal.haySesionGuardada(context)) {
            val sesion = SesionLocal.obtenerSesionGuardada(context)
            if (sesion != null && sesion.refreshToken.isNotBlank()) {
                cargando = true
                val resultado = SupabaseApi.refrescarSesion(sesion.refreshToken)
                resultado.onSuccess { nuevaSesion ->
                    SesionLocal.actualizarTokens(
                        context = context,
                        accessToken = nuevaSesion.accessToken,
                        refreshToken = nuevaSesion.refreshToken
                    )
                    cargando = false
                    onLoginExitoso()
                }
                resultado.onFailure {
                    // Token expirado — mostramos login normal
                    SesionLocal.cerrarSesion(context)
                    cargando = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("Click")
                    }
                    withStyle(style = SpanStyle(color = lightPurple)) {
                        append("Work")
                    }
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Accede a tu cuenta y encuentra\noportunidades cerca de ti",
                color = textGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = {
                    Text(text = "Correo electrónico", color = textGray)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = primaryPurple
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = primaryPurple
                ),
                singleLine = true,
                enabled = !cargando
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(text = "Contraseña", color = textGray)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = primaryPurple
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { mostrarPassword = !mostrarPassword },
                        enabled = !cargando
                    ) {
                        Icon(
                            imageVector = if (mostrarPassword)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = null,
                            tint = textGray
                        )
                    }
                },
                visualTransformation = if (mostrarPassword)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = primaryPurple
                ),
                singleLine = true,
                enabled = !cargando
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Fila: Recordar sesión + ¿Olvidaste contraseña? ──────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = recordarSesion,
                    onCheckedChange = { recordarSesion = it },
                    enabled = !cargando,
                    colors = CheckboxDefaults.colors(
                        checkedColor = primaryPurple,
                        uncheckedColor = textGray,
                        checkmarkColor = Color.White
                    )
                )

                Text(
                    text = "Recordar mi cuenta",
                    color = textGray,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !cargando) {
                            recordarSesion = !recordarSesion
                        }
                )

                TextButton(
                    onClick = { if (!cargando) onOlvidoPassword() },
                    enabled = !cargando
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = lightPurple,
                        fontSize = 13.sp
                    )
                }
            }

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val gradient = Brush.horizontalGradient(
                listOf(lightPurple, primaryPurple)
            )

            // ── Botón Iniciar sesión ─────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        correo.isBlank() || password.isBlank() -> {
                            error = "Ingresa tu correo y contraseña"
                        }
                        else -> {
                            error = ""
                            cargando = true
                            scope.launch {
                                val resultado = SupabaseApi.iniciarSesion(
                                    correo = correo.trim(),
                                    password = password
                                )
                                resultado.onSuccess { sesion ->
                                    SesionLocal.guardarSesion(
                                        context = context,
                                        sesion = sesion,
                                        recordar = recordarSesion
                                    )
                                    cargando = false
                                    onLoginExitoso()
                                }
                                resultado.onFailure { exception ->
                                    cargando = false
                                    error = exception.message
                                        ?: "Correo o contraseña incorrectos"
                                }
                            }
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = gradient,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (cargando) "Iniciando sesión..." else "Iniciar sesión",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
                Text(
                    text = " o continúa con ",
                    color = textGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botón Google OAuth ───────────────────────────────────────────
            OutlinedButton(
                onClick = {
                    if (!cargando) {
                        val url = SupabaseApi.obtenerUrlGoogleOAuth()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = inputBackgroundColor,
                    contentColor = Color.White
                ),
                border = null
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Logo de Google",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continuar con Google",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿No tienes cuenta? ", color = textGray)
                Text(
                    text = "Crear cuenta ➔",
                    color = lightPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (!cargando) onCrearCuenta()
                    }
                )
            }
        }
    }
}
