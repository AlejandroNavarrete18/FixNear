package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.Image
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
<<<<<<< HEAD
import androidx.compose.ui.layout.ContentScale
=======
import androidx.compose.ui.platform.LocalContext
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

<<<<<<< HEAD
    // Colores del formulario
=======
    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Definición de colores del nuevo diseño
    val backgroundColor = Color(0xFF0B0F19)
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
    val inputBackgroundColor = Color(0xFF131826)
    val lightPurple = Color(0xFF8B5CF6)
    val textGray = Color(0xFF9CA3AF)
    val clickWorkBlue = Color(0xFF3B82F6)

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Capa de Fondo (Tu nueva imagen completa)
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

<<<<<<< HEAD
            // Este Spacer empuja todo el formulario hacia abajo,
            // dejando visible la curva y los edificios de tu imagen.
            Spacer(modifier = Modifier.weight(1f))
=======
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
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = {
                    Text("Correo electrónico", color = textGray)
                },
                leadingIcon = {
<<<<<<< HEAD
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = clickWorkBlue)
=======
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = primaryPurple
                    )
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = clickWorkBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                enabled = !cargando
            )

            // Redujimos este espacio a 12.dp
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text("Contraseña", color = textGray)
                },
                leadingIcon = {
<<<<<<< HEAD
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = clickWorkBlue)
=======
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = primaryPurple
                    )
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            mostrarPassword = !mostrarPassword
                        },
                        enabled = !cargando
                    ) {
                        Icon(
<<<<<<< HEAD
                            imageVector = if (mostrarPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
=======
                            imageVector = if (mostrarPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
                            contentDescription = null,
                            tint = textGray
                        )
                    }
                },
                visualTransformation = if (mostrarPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = clickWorkBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                enabled = !cargando
            )

<<<<<<< HEAD
            // ¿Olvidaste tu contraseña?
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { onOlvidoPassword() }) {
                    Text("¿Olvidaste tu contraseña?", color = clickWorkBlue, fontSize = 13.sp)
                }
            }

            // Redujimos este espacio a 4.dp
            Spacer(modifier = Modifier.height(4.dp))

            // Botón Iniciar Sesión (Ahora color Azul Sólido)
=======
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = { /* TODO */ },
                    enabled = !cargando
                ) {
                    Text("¿Olvidaste tu contraseña?", color = lightPurple)
                }
            }

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

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

>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
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
                                        sesion = sesion
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
<<<<<<< HEAD
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = clickWorkBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Iniciar sesión",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
=======
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
                            gradient,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (cargando) {
                                "Iniciando sesión..."
                            } else {
                                "Iniciar sesión"
                            },
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
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
                }
            }

            // Redujimos este espacio a 16.dp
            Spacer(modifier = Modifier.height(16.dp))

<<<<<<< HEAD
            // Separador "O continúa con"
=======
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
<<<<<<< HEAD
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF374151))
=======
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.DarkGray
                )

>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
                Text(
                    text = " O continúa con ",
                    color = textGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
<<<<<<< HEAD
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF374151))
=======

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.DarkGray
                )
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
            }

            // Redujimos este espacio a 16.dp
            Spacer(modifier = Modifier.height(16.dp))

<<<<<<< HEAD
            // Botones de Google y Facebook
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
=======
            OutlinedButton(
                onClick = { /* TODO: Lógica de Google luego */ },
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
>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
            ) {
                // Botón Google
                OutlinedButton(
                    onClick = { /* Lógica de Google */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = inputBackgroundColor,
                        contentColor = Color.White
                    ),
                    border = null,
                    contentPadding = PaddingValues(horizontal = 8.dp)
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

                // Botón Facebook
                OutlinedButton(
                    onClick = { /* Lógica de Facebook */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = inputBackgroundColor,
                        contentColor = Color.White
                    ),
                    border = null,
                    contentPadding = PaddingValues(horizontal = 8.dp)
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

            // Redujimos este espacio a 24.dp
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
<<<<<<< HEAD
                Text("¿No tienes cuenta? ", color = textGray, fontSize = 14.sp)
=======
                Text("¿No tienes cuenta? ", color = textGray)

>>>>>>> 43ad27b ("Mapa Real de LAS 2 SECCIONES Y IMPLEMENTACION BD")
                Text(
                    text = "Crear cuenta",
                    color = lightPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (!cargando) {
                            onCrearCuenta()
                        }
                    }
                )
            }

            // Espacio extra al final reducido a 16.dp
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}