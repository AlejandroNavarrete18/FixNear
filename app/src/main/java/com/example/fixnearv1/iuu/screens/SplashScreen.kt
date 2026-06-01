package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importamos .sp
import com.example.fixnearv1.R
import com.example.fixnearv1.modelo.ui.theme.TitleStyle
import com.example.fixnearv1.modelo.ui.theme.TitleWorkStyle
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit // 👇 CAMBIO AQUÍ: Renombramos el parámetro
) {
    // 1. EL TEMPORIZADOR (LaunchedEffect) - Mismo tiempo
    LaunchedEffect(key1 = true) {
        delay(2500) // 2.5 segundos
        onNavigateToLogin() // 👇 CAMBIO AQUÍ: Ejecutamos el nuevo parámetro
    }

    // 2. EL DISEÑO VISUAL
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A2238)), // Fondo azul marino oscuro
        contentAlignment = Alignment.Center // Centra todo exactamente en medio
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Achicado de 80.dp a 60.dp
            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo de carga",
                modifier = Modifier.size(60.dp)
            )

            // Espacio reducido de 16.dp a 12.dp
            Spacer(modifier = Modifier.height(12.dp))

            // Nombre de la app con texto achicado (usando copy para cambiar fontSize)
            Text(
                text = buildAnnotatedString {
                    // Achicamos a 28.sp el texto "Click"
                    withStyle(style = TitleStyle.toSpanStyle().copy(fontSize = 28.sp)) {
                        append("Click")
                    }
                    // Achicamos a 28.sp el texto "Work"
                    withStyle(style = TitleWorkStyle.toSpanStyle().copy(fontSize = 28.sp)) {
                        append("Work")
                    }
                }
            )
        }
    }
}