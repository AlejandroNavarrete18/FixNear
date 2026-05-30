package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle // <-- Nueva importación
import androidx.compose.ui.text.buildAnnotatedString // <-- Nueva importación
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle // <-- Nueva importación
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onCuentaCreada: () -> Unit,
    onRegresar: () -> Unit
) {
    // Estados de los campos
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var tipoUsuario by remember { mutableStateOf("Cliente") }
    var error by remember { mutableStateOf("") }

    // Estados para la visibilidad de las contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

    // Colores para el TEMA OSCURO
    val backgroundColor = Color(0xFF0D0F17)
    val fieldBackgroundColor = Color(0xFF1A1C29)
    val primaryPurple = Color(0xFF8A4FFF)
    val textGray = Color(0xFFA0AEC0)
    val textWhite = Color.White
    val workBlue = Color(0xFF3DA9FC)

    // Gradiente para el botón
    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFD478FF), Color(0xFF6B38FB))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = textWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- SECCIÓN DE LOGO Y NOMBRE ---
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_clickwork_logo),
                contentDescription = "Logo de ClickWork",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TEXTO "ClickWork" CON DOS COLORES
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = textWhite)) {
                        append("Click")
                    }
                    withStyle(style = SpanStyle(color = workBlue)) {
                        append("Work")
                    }
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crea una cuenta para comenzar",
                fontSize = 14.sp,
                color = textGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            // -----------------------------------------------

            // FORMULARIO (Sin tarjeta blanca, directo sobre el fondo)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // CAMPO NOMBRE
                CustomOutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre completo",
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = primaryPurple) },
                    fieldBackgroundColor = fieldBackgroundColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO CORREO
                CustomOutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo electrónico",
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = primaryPurple) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    fieldBackgroundColor = fieldBackgroundColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO TELÉFONO
                CustomOutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "Teléfono",
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = primaryPurple) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    fieldBackgroundColor = fieldBackgroundColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO CONTRASEÑA
                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryPurple) },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = "Ver contraseña", tint = textGray)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    fieldBackgroundColor = fieldBackgroundColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO CONFIRMAR CONTRASEÑA
                CustomOutlinedTextField(
                    value = confirmar,
                    onValueChange = { confirmar = it },
                    label = "Confirmar contraseña",
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryPurple) },
                    trailingIcon = {
                        val icon = if (confirmarVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        IconButton(onClick = { confirmarVisible = !confirmarVisible }) {
                            Icon(icon, contentDescription = "Ver contraseña", tint = textGray)
                        }
                    },
                    visualTransformation = if (confirmarVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    fieldBackgroundColor = fieldBackgroundColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // TIPO DE USUARIO
                Text(
                    text = "Tipo de usuario",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    UserTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Cliente",
                        subtitle = "Busco servicios",
                        icon = Icons.Outlined.Person,
                        isSelected = tipoUsuario == "Cliente",
                        selectedColor = primaryPurple,
                        unselectedCardColor = fieldBackgroundColor,
                        onClick = { tipoUsuario = "Cliente" }
                    )

                    UserTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Trabajador",
                        subtitle = "Ofrezco servicios",
                        icon = Icons.Outlined.Build,
                        isSelected = tipoUsuario == "Trabajador",
                        selectedColor = primaryPurple,
                        unselectedCardColor = fieldBackgroundColor,
                        onClick = { tipoUsuario = "Trabajador" }
                    )
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÓN PRINCIPAL CON DEGRADADO
                Button(
                    onClick = {
                        when {
                            nombre.isBlank() || correo.isBlank() || telefono.isBlank() ||
                                    password.isBlank() || confirmar.isBlank() -> {
                                error = "Completa todos los campos"
                            }
                            password != confirmar -> {
                                error = "Las contraseñas no coinciden"
                            }
                            else -> {
                                error = ""
                                onCuentaCreada()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(brush = buttonGradient, shape = RoundedCornerShape(28.dp)), // Aplica el gradiente aquí
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Contenedor transparente
                    contentPadding = PaddingValues() // Quitar padding por defecto
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // TEXTO DE INICIO DE SESIÓN
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿Ya tienes una cuenta? ", color = textGray, fontSize = 14.sp)
                Text(
                    text = "Iniciar sesión",
                    color = Color(0xFFD478FF), // Color rosa claro para que resalte
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegresar() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BADGE DE SEGURIDAD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = primaryPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Tu información está segura", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textWhite)
                    Text("Protegemos tus datos personales", fontSize = 11.sp, color = textGray)
                }
            }
        }
    }
}

// Componente reutilizable para los TextFields actualizado a modo oscuro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    fieldBackgroundColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color(0xFFA0AEC0)) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF8A4FFF),
            unfocusedBorderColor = Color.Transparent, // Sin borde si no está enfocado
            focusedContainerColor = fieldBackgroundColor,
            unfocusedContainerColor = fieldBackgroundColor,
            focusedTextColor = Color.White,   // Texto escrito en blanco
            unfocusedTextColor = Color.White, // Texto escrito en blanco
            cursorColor = Color(0xFF8A4FFF)
        )
    )
}

// Componente para las tarjetas de "Cliente" y "Trabajador" adaptado al modo oscuro
@Composable
fun UserTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedCardColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) selectedColor else Color.Transparent
    val backgroundColor = if (isSelected) selectedColor.copy(alpha = 0.15f) else unselectedCardColor

    Surface(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        color = backgroundColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) selectedColor else Color(0xFFA0AEC0),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFFA0AEC0) // Blanco si está seleccionado
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}