package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.R
import com.example.fixnearv1.modelo.ui.theme.TitleStyle
import com.example.fixnearv1.modelo.ui.theme.TitleWorkStyle
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    // 1. EL TEMPORIZADOR (LaunchedEffect)
    LaunchedEffect(key1 = true) {
        delay(500)
        onNavigateToLogin()
    }

    // Creamos el degradado (difuminado) con los colores de tu diseño
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF020B2F), // Azul noche arriba
            Color(0xFF031A5A), // Azul más claro en el centro
            Color(0xFF010818)  // Casi negro abajo
        )
    )

    // 2. EL DISEÑO VISUAL
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient), // Aplicamos el fondo difuminado
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo ajustado a un tamaño intermedio (100.dp)
            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo de carga",
                modifier = Modifier.size(100.dp)
            )


            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = buildAnnotatedString {
                    withStyle(style = TitleStyle.toSpanStyle().copy(fontSize = 24.sp)) {
                        append("Click")
                    }
                    withStyle(style = TitleWorkStyle.toSpanStyle().copy(fontSize = 24.sp)) {
                        append("Work")
                    }
                }
            )
        }
    }
}