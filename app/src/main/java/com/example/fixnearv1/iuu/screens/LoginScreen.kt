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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    onOlvidoPassword: () -> Unit
) {
    // Estados de UI y Lógica
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var recordarSesion by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Colores del formulario
    val inputBackgroundColor = Color(0xFF131826)
    val lightPurple = Color(0xFF8B5CF6)
    val textGray = Color(0xFF9CA3AF)
    val clickWorkBlue = Color(0xFF3B82F6)

    // Auto-login de la lógica vieja
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
                    SesionLocal.cerrarSesion(context)
                    cargando = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Capa de Fondo
        Image(
            painter = painterResource(id = R.drawable.background_login),
            contentDescription = "Fondo completo de Login",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Capa del Formulario (Alineada hacia abajo)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Empuja todo el formulario hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // Campo de Correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("Correo electrónico", color = textGray) },
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = clickWorkBlue)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = clickWorkBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = clickWorkBlue
                ),
                singleLine = true,
                enabled = !cargando // Bloqueo durante carga
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", color = textGray) },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = clickWorkBlue)
                },
                trailingIcon = {
                    IconButton(
                        onClick = { mostrarPassword = !mostrarPassword },
                        enabled = !cargando
                    ) {
                        Icon(
                            imageVector = if (mostrarPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = textGray
                        )
                    }
                },
                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = clickWorkBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = clickWorkBlue
                ),
                singleLine = true,
                enabled = !cargando // Bloqueo durante carga
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Fila: Recordar sesión + ¿Olvidaste contraseña?
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = recordarSesion,
                    onCheckedChange = { recordarSesion = it },
                    enabled = !cargando,
                    colors = CheckboxDefaults.colors(
                        checkedColor = clickWorkBlue,
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
                        color = clickWorkBlue, // Adaptado al nuevo color
                        fontSize = 13.sp
                    )
                }
            }

            // Mensaje de Error
            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Iniciar Sesión con lógica vieja
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
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = clickWorkBlue,
                    disabledContainerColor = clickWorkBlue.copy(alpha = 0.5f)
                )
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
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Separador "O continúa con"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF374151))
                Text(
                    text = " O continúa con ",
                    color = textGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF374151))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de Google y Facebook
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Google (Con lógica de Intent)
                OutlinedButton(
                    onClick = {
                        if (!cargando) {
                            val url = SupabaseApi.obtenerUrlGoogleOAuth()
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = inputBackgroundColor,
                        contentColor = Color.White
                    ),
                    border = null,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    enabled = !cargando
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Google", fontSize = 14.sp, maxLines = 1)
                }

                // Botón Facebook (Solo Visual)
                OutlinedButton(
                    onClick = { /* Lógica de Facebook pendiente */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = inputBackgroundColor,
                        contentColor = Color.White
                    ),
                    border = null,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    enabled = !cargando
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook_logo),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Facebook", fontSize = 14.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Crear Cuenta
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta? ", color = textGray, fontSize = 14.sp)
                Text(
                    text = "Crear cuenta",
                    color = lightPurple, // Conservado el tono morado que tenías para este texto
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !cargando) { onCrearCuenta() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}