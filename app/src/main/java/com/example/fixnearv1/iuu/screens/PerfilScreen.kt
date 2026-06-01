package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.utils.PerfilUsuario
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SupabaseApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onRegresar: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current

    var perfil by remember {
        mutableStateOf<PerfilUsuario?>(null)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var errorCarga by remember {
        mutableStateOf("")
    }

    var cuentaVerificada by remember {
        mutableStateOf(false)
    }

    var mostrarDialogo by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val userId = SesionLocal.obtenerUserId(context)
        val token = SesionLocal.obtenerAccessToken(context)

        if (userId.isBlank() || token.isBlank()) {
            errorCarga = "No hay sesión activa"
            cargando = false
            return@LaunchedEffect
        }

        val resultado = SupabaseApi.obtenerPerfil(
            userId = userId,
            accessToken = token
        )

        resultado.onSuccess {
            perfil = it
        }

        resultado.onFailure {
            errorCarga = it.message ?: "No se pudieron cargar tus datos"
        }

        cargando = false
    }

    val nombreCompleto = when {
        cargando -> "Cargando..."
        perfil != null -> perfil!!.nombre
        else -> "Usuario"
    }

    val nombres = obtenerNombresPerfil(perfil?.nombre ?: "")
    val apellidos = obtenerApellidosPerfil(perfil?.nombre ?: "")

    val telefono = perfil?.telefono
        ?.takeIf { it.isNotBlank() }
        ?: "Sin teléfono"

    val correo = perfil?.correo
        ?.takeIf { it.isNotBlank() }
        ?: "Sin correo"

    val tipoUsuario = perfil?.tipoUsuario
        ?.takeIf { it.isNotBlank() }
        ?: "Cliente"

    if (mostrarDialogo.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = ""
            },
            title = {
                Text(mostrarDialogo)
            },
            text = {
                Text("Funcionalidad simulada para demostración.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = ""
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mi perfil")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onRegresar
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                ),
            contentPadding = PaddingValues(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Surface(
                    modifier = Modifier.size(105.dp),
                    shape = CircleShape,
                    color = Color(0xFF334155)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto de perfil",
                            tint = Color.White,
                            modifier = Modifier.size(88.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = nombreCompleto,
                    color = Color.White,
                    fontSize = 25.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$tipoUsuario FixNear",
                    color = Color.LightGray,
                    fontSize = 15.sp
                )

                if (errorCarga.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = errorCarga,
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row {

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFF334155)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "4.97",
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFF334155)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint =
                                    if (cuentaVerificada)
                                        Color(0xFF38BDF8)
                                    else
                                        Color.LightGray
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text =
                                    if (cuentaVerificada)
                                        "Verificada"
                                    else
                                        "No verificada",
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                PerfilSeccion {

                    PerfilFila(
                        titulo = "Nombre",
                        valor = nombres.ifBlank { nombreCompleto }
                    ) {
                        mostrarDialogo = "Editar nombre"
                    }

                    PerfilFila(
                        titulo = "Apellido",
                        valor = apellidos.ifBlank { "Sin apellido" }
                    ) {
                        mostrarDialogo = "Editar apellido"
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PerfilSeccion {

                    PerfilFila(
                        titulo = "Número de teléfono",
                        valor = telefono
                    ) {
                        mostrarDialogo = "Editar teléfono"
                    }

                    PerfilFila(
                        titulo = "Correo electrónico",
                        valor = correo
                    ) {
                        cuentaVerificada = true
                        mostrarDialogo = "Correo verificado correctamente"
                    }

                    PerfilFila(
                        titulo = "Tipo de usuario",
                        valor = tipoUsuario
                    ) {
                        mostrarDialogo = "Tipo de usuario"
                    }

                    PerfilFila(
                        titulo = "CURP",
                        valor = "Sin verificar"
                    ) {
                        mostrarDialogo = "Verificar CURP"
                    }

                    PerfilFila(
                        titulo = "Cambiar contraseña",
                        valor = ""
                    ) {
                        mostrarDialogo = "Cambiar contraseña"
                    }

                    PerfilFila(
                        titulo = "Llave de acceso",
                        valor = "No creada"
                    ) {
                        mostrarDialogo = "Crear llave de acceso"
                    }

                    PerfilFila(
                        titulo = "Mis redes sociales",
                        valor = ""
                    ) {
                        mostrarDialogo = "Redes sociales"
                    }

                    PerfilFila(
                        titulo = "Mis dispositivos",
                        valor = ""
                    ) {
                        mostrarDialogo = "Dispositivos vinculados"
                    }

                    PerfilFila(
                        titulo = "Servicios solicitados",
                        valor = "4"
                    ) {
                        mostrarDialogo = "Historial de servicios"
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        SesionLocal.cerrarSesion(context)
                        onCerrarSesion()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334155)
                    )
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Cerrar sesión",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        mostrarDialogo = "Eliminar mi cuenta"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Eliminar mi cuenta")
                }

                Spacer(modifier = Modifier.height(35.dp))
            }
        }
    }
}

fun obtenerNombresPerfil(nombreCompleto: String): String {
    val partes = nombreCompleto
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        partes.isEmpty() -> ""
        partes.size >= 3 -> partes.take(2).joinToString(" ")
        else -> partes.first()
    }
}

fun obtenerApellidosPerfil(nombreCompleto: String): String {
    val partes = nombreCompleto
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        partes.size >= 3 -> partes.drop(2).joinToString(" ")
        partes.size == 2 -> partes.drop(1).joinToString(" ")
        else -> ""
    }
}

@Composable
fun PerfilSeccion(
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF334155)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = contenido
        )
    }
}

@Composable
fun PerfilFila(
    titulo: String,
    valor: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            if (valor.isNotEmpty()) {
                Text(
                    text = valor,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(
            color = Color(0xFF475569)
        )
    }
}