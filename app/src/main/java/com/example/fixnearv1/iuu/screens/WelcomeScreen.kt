package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.R
import com.example.fixnearv1.iuu.components.BotonPrincipal
import com.example.fixnearv1.iuu.components.BotonSecundario
import com.example.fixnearv1.modelo.ui.theme.TitleStyle
import com.example.fixnearv1.modelo.ui.theme.TitleWorkStyle

@Composable
fun WelcomeScreen(
    onIniciarSesion: () -> Unit,
    onCrearCuenta: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // 1. DEGRADADO DE FONDO
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A2238), // Centro: Tono azul marino oscuro
                        Color(0xFF090D16)  // Bordes: Azul noche casi negro
                    ),
                    radius = 1200f
                )
            )
    ) {

        // 2. DIBUJO DE LAS LÍNEAS CURVAS EN EL FONDO
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Línea morada exterior
            drawArc(
                color = Color(0xFF673AB7).copy(alpha = 0.15f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(-canvasWidth * 0.3f, canvasHeight * 0.78f),
                size = Size(canvasWidth * 1.6f, canvasHeight * 0.5f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Línea azul intermedia
            drawArc(
                color = Color(0xFF42A5F5).copy(alpha = 0.1f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(0f, canvasHeight * 0.83f),
                size = Size(canvasWidth * 1.2f, canvasHeight * 0.4f),
                style = Stroke(width = 1.dp.toPx())
            )

            // Línea morada interior
            drawArc(
                color = Color(0xFF673AB7).copy(alpha = 0.08f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(canvasWidth * 0.1f, canvasHeight * 0.88f),
                size = Size(canvasWidth * 0.8f, canvasHeight * 0.3f),
                style = Stroke(width = 0.5.dp.toPx())
            )
        }

        //(Logo, Textos y Botones)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), // Padding solo a los lados
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Espacio fijo arriba para centrar mejor el conjunto
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo ClickWork",
                modifier = Modifier.size(115.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = TitleStyle.toSpanStyle()) {
                        append("Click")
                    }
                    withStyle(style = TitleWorkStyle.toSpanStyle()) {
                        append("Work")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Encuentra servicios y empleos cerca de ti",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )

            // 3. POSICIÓN DE BOTONES
            Spacer(modifier = Modifier.height(70.dp))

            BotonPrincipal(
                text = "Iniciar sesión",
                onClick = onIniciarSesion,
                iconRight = R.drawable.ic_arrow_right
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotonSecundario(
                text = "Crear cuenta",
                onClick = onCrearCuenta,
                iconLeft = R.drawable.ic_user_profile,
                iconRight = R.drawable.ic_arrow_right
            )

            // Margen inferior para que quede equilibrado visualmente
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}