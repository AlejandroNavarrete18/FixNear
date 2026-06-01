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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.R

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

    // Colores del formulario
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

            // Este Spacer empuja todo el formulario hacia abajo,
            // dejando visible la curva y los edificios de tu imagen.
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
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            // Redujimos este espacio a 12.dp
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
                    IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
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
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            // ¿Olvidaste tu contraseña?
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { onOlvidoPassword() }) {
                    Text("¿Olvidaste tu contraseña?", color = clickWorkBlue, fontSize = 13.sp)
                }
            }

            // Redujimos este espacio a 4.dp
            Spacer(modifier = Modifier.height(4.dp))

            // Botón Iniciar Sesión (Ahora color Azul Sólido)
            Button(
                onClick = onLoginExitoso,
                modifier = Modifier
                    .fillMaxWidth()
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
                }
            }

            // Redujimos este espacio a 16.dp
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

            // Redujimos este espacio a 16.dp
            Spacer(modifier = Modifier.height(16.dp))

            // Botones de Google y Facebook
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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

            // Footer Crear Cuenta
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta? ", color = textGray, fontSize = 14.sp)
                Text(
                    text = "Crear cuenta",
                    color = lightPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onCrearCuenta() }
                )
            }

            // Espacio extra al final reducido a 16.dp
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}