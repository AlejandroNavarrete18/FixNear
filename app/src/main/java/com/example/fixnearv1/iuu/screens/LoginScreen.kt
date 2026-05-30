package com.example.fixnearv1.iuu.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onCrearCuenta: () -> Unit,
    onOlvidoPassword: () -> Unit // <-- Parámetro agregado
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    // Definición de colores del nuevo diseño
    val backgroundColor = Color(0xFF0B0F19)
    val inputBackgroundColor = Color(0xFF131826)
    val primaryPurple = Color(0xFF8A2BE2)
    val lightPurple = Color(0xFFB388FF)
    val textGray = Color(0xFFA0AABF)

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

            // Logo de la App
            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre
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

            // Campo de Correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("Correo electrónico", color = textGray) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = primaryPurple)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña", color = textGray) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = primaryPurple)
                },
                trailingIcon = {
                    IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                        Icon(
                            imageVector = if (mostrarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = textGray
                        )
                    }
                },
                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    focusedBorderColor = primaryPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ¿Olvidaste tu contraseña?
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { onOlvidoPassword() }) { // <-- CAMBIO APLICADO AQUÍ
                    Text("¿Olvidaste tu contraseña?", color = lightPurple)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Iniciar Sesión con Degradado
            val gradient = Brush.horizontalGradient(listOf(lightPurple, primaryPurple))
            Button(
                onClick = onLoginExitoso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
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
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Separador "o continúa con"
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

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Google
            OutlinedButton(
                onClick = { /* TODO: Lógica de Google luego */ },
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

                Text("Continuar con Google", color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer Crear Cuenta
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta? ", color = textGray)
                Text(
                    text = "Crear cuenta ➔",
                    color = lightPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onCrearCuenta() }
                )
            }
        }
    }
}