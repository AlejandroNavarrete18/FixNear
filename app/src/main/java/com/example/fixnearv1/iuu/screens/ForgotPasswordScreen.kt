package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.R
import com.example.fixnearv1.iuu.components.BotonPrincipal
import com.example.fixnearv1.iuu.components.CustomTextField
import com.example.fixnearv1.modelo.ui.theme.ClickWorkPurpleMain

@Composable
fun ForgotPasswordScreen(onRegresar: () -> Unit) {
    // Colores de fondo
    val backgroundColor = Color(0xFF0F111A)
    val textColor = Color.White
    val subtitleColor = Color(0xFFA0A0A0)
    val bannerBackground = Color(0xFF1C1E2B)

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp), // Margen general de la pantalla
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Botón de retroceso
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            IconButton(onClick = { onRegresar() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = ClickWorkPurpleMain
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre flecha y logo

        // 2. Logo de ClickWork
        Image(
            painter = painterResource(id = R.drawable.ic_clickwork_logo),
            contentDescription = "Logo ClickWork",
            modifier = Modifier.size(60.dp) // Reducido ligeramente para dar más espacio
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Títulos
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = textColor,
            fontSize = 22.sp, // Ajuste sutil de tamaño
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No te preocupes, sucede. Ingresa tu correo electrónico y te enviaremos instrucciones para restablecer tu contraseña.",
            color = subtitleColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp) // Un poco menos de padding lateral
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espacio antes de la ilustración

        // 4. Ilustración Central (Ajustada)
        Image(
            painter = painterResource(id = R.drawable.forgot_password_illustration),
            contentDescription = "Ilustración Enviar Correo",
            modifier = Modifier
                .height(140.dp) // Forzamos la altura para que no desmadre la pantalla
                .fillMaxWidth(), // Que tome el ancho disponible pero respetando la altura
            contentScale = ContentScale.Fit // Asegura que la imagen se adapte sin cortarse
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espacio después de la ilustración

        // 5. Input de Correo
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = ClickWorkPurpleMain
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Botón Enviar
        BotonPrincipal(
            text = "Enviar instrucciones",
            onClick = { /* TODO: Lógica de envío de correo */ },
            iconRight = R.drawable.ic_arrow_right
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Enlace para volver a Iniciar Sesión
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿Recordaste tu contraseña? ", color = subtitleColor, fontSize = 14.sp)
            Text(
                text = "Iniciar sesión",
                color = ClickWorkPurpleMain,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onRegresar()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 8. Banner de Seguridad Inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bannerBackground, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Seguridad",
                tint = ClickWorkPurpleMain,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Tu información está segura", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "Protegemos tus datos personales y nunca compartimos tu información.",
                    color = subtitleColor,
                    fontSize = 12.sp,
                    lineHeight = 16.sp // Para que el texto pequeño no se vea tan pegado
                )
            }
        }
    }
}