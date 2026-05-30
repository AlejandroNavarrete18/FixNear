package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.R
import com.example.fixnearv1.iuu.components.BotonPrincipal
import com.example.fixnearv1.iuu.components.BotonSecundario
import com.example.fixnearv1.ui.theme.ClickWorkBackground
import com.example.fixnearv1.ui.theme.TitleStyle
import com.example.fixnearv1.ui.theme.TitleWorkStyle

@Composable
fun WelcomeScreen(
    onIniciarSesion: () -> Unit,
    onCrearCuenta: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ClickWorkBackground) // Usamos tu nuevo fondo oscuro
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Este Spacer empuja el contenido hacia el centro
            Spacer(modifier = Modifier.weight(1f))

            // 1. Nuevo Logotipo en vector
            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo ClickWork",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Título principal "ClickWork" con su estilo bicolor
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

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Subtítulo
            Text(
                text = "Encuentra servicios y empleos cerca de ti",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )

            // Este Spacer empuja los botones hacia la parte inferior
            Spacer(modifier = Modifier.weight(1f))

            // 4. Nuevo Botón Principal
            BotonPrincipal(
                text = "Iniciar sesión",
                onClick = onIniciarSesion,
                iconRight = R.drawable.ic_arrow_right
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Nuevo Botón Secundario
            BotonSecundario(
                text = "Crear cuenta",
                onClick = onCrearCuenta,
                iconLeft = R.drawable.ic_user_profile,
                iconRight = R.drawable.ic_arrow_right
            )

            // Un pequeño margen inferior para que los botones no queden pegados a la pantalla
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}