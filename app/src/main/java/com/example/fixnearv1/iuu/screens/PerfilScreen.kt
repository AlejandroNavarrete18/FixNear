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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.utils.PerfilUsuario
import com.example.fixnearv1.utils.SesionLocal
import com.example.fixnearv1.utils.SupabaseApi
import kotlinx.coroutines.launch

// ─── Tipos de diálogo de edición ────────────────────────────────────────────
private sealed class DialogoEdicion {
    object Ninguno : DialogoEdicion()
    object Nombre : DialogoEdicion()
    object Apellido : DialogoEdicion()
    object Correo : DialogoEdicion()
    object Password : DialogoEdicion()
    data class Info(val titulo: String, val mensaje: String) : DialogoEdicion()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onRegresar: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var perfil by remember { mutableStateOf<PerfilUsuario?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf("") }
    var cuentaVerificada by remember { mutableStateOf(false) }

    // Estado del diálogo activo
    var dialogoActivo by remember { mutableStateOf<DialogoEdicion>(DialogoEdicion.Ninguno) }

    // Carga inicial del perfil
    LaunchedEffect(Unit) {
        val userId = SesionLocal.obtenerUserId(context)
        val token = SesionLocal.obtenerAccessToken(context)

        if (userId.isBlank() || token.isBlank()) {
            errorCarga = "No hay sesión activa"
            cargando = false
            return@LaunchedEffect
        }

        val resultado = SupabaseApi.obtenerPerfil(userId = userId, accessToken = token)
        resultado.onSuccess { perfil = it }
        resultado.onFailure { errorCarga = it.message ?: "No se pudieron cargar tus datos" }
        cargando = false
    }

    val nombreCompleto = when {
        cargando -> "Cargando..."
        perfil != null -> perfil!!.nombre
        else -> "Usuario"
    }

    val nombres = obtenerNombresPerfil(perfil?.nombre ?: "")
    val apellidos = obtenerApellidosPerfil(perfil?.nombre ?: "")
    val telefono = perfil?.telefono?.takeIf { it.isNotBlank() } ?: "Sin teléfono"
    val correo = perfil?.correo?.takeIf { it.isNotBlank() } ?: "Sin correo"
    val tipoUsuario = perfil?.tipoUsuario?.takeIf { it.isNotBlank() } ?: "Cliente"

    // ── Diálogos de edición ──────────────────────────────────────────────────
    when (val dialogo = dialogoActivo) {

        is DialogoEdicion.Nombre -> {
            DialogoEditarTexto(
                titulo = "Editar nombre",
                etiqueta = "Nombre(s)",
                valorInicial = nombres,
                onGuardar = { nuevoNombre ->
                    scope.launch {
                        val apellidoActual = obtenerApellidosPerfil(perfil?.nombre ?: "")
                        val nombreFinal = if (apellidoActual.isBlank())
                            nuevoNombre.trim()
                        else
                            "${nuevoNombre.trim()} $apellidoActual"

                        val userId = SesionLocal.obtenerUserId(context)
                        val token = SesionLocal.obtenerAccessToken(context)

                        val result = SupabaseApi.actualizarNombrePerfil(
                            userId = userId,
                            accessToken = token,
                            nuevoNombre = nombreFinal
                        )
                        result.onSuccess {
                            perfil = perfil?.copy(nombre = nombreFinal)
                            dialogoActivo = DialogoEdicion.Info(
                                "Nombre actualizado",
                                "Tu nombre se actualizó correctamente."
                            )
                        }
                        result.onFailure {
                            dialogoActivo = DialogoEdicion.Info(
                                "Error",
                                it.message ?: "No se pudo actualizar el nombre."
                            )
                        }
                    }
                },
                onDismiss = { dialogoActivo = DialogoEdicion.Ninguno }
            )
        }

        is DialogoEdicion.Apellido -> {
            DialogoEditarTexto(
                titulo = "Editar apellido",
                etiqueta = "Apellido(s)",
                valorInicial = apellidos,
                onGuardar = { nuevoApellido ->
                    scope.launch {
                        val nombreActual = obtenerNombresPerfil(perfil?.nombre ?: "")
                        val nombreFinal = if (nuevoApellido.isBlank())
                            nombreActual
                        else
                            "$nombreActual ${nuevoApellido.trim()}"

                        val userId = SesionLocal.obtenerUserId(context)
                        val token = SesionLocal.obtenerAccessToken(context)

                        val result = SupabaseApi.actualizarNombrePerfil(
                            userId = userId,
                            accessToken = token,
                            nuevoNombre = nombreFinal
                        )
                        result.onSuccess {
                            perfil = perfil?.copy(nombre = nombreFinal)
                            dialogoActivo = DialogoEdicion.Info(
                                "Apellido actualizado",
                                "Tu apellido se actualizó correctamente."
                            )
                        }
                        result.onFailure {
                            dialogoActivo = DialogoEdicion.Info(
                                "Error",
                                it.message ?: "No se pudo actualizar el apellido."
                            )
                        }
                    }
                },
                onDismiss = { dialogoActivo = DialogoEdicion.Ninguno }
            )
        }

        is DialogoEdicion.Correo -> {
            DialogoEditarTexto(
                titulo = "Cambiar correo electrónico",
                etiqueta = "Nuevo correo",
                valorInicial = perfil?.correo ?: "",
                onGuardar = { nuevoCorreo ->
                    scope.launch {
                        if (!nuevoCorreo.contains("@")) {
                            dialogoActivo = DialogoEdicion.Info(
                                "Correo inválido",
                                "Ingresa un correo electrónico válido."
                            )
                            return@launch
                        }
                        val userId = SesionLocal.obtenerUserId(context)
                        val token = SesionLocal.obtenerAccessToken(context)

                        val result = SupabaseApi.actualizarCorreo(
                            accessToken = token,
                            userId = userId,
                            nuevoCorreo = nuevoCorreo.trim()
                        )
                        result.onSuccess {
                            perfil = perfil?.copy(correo = nuevoCorreo.trim())
                            dialogoActivo = DialogoEdicion.Info(
                                "Correo actualizado",
                                "Se enviará un enlace de confirmación a $nuevoCorreo. " +
                                    "Confirma el cambio desde tu bandeja de entrada."
                            )
                        }
                        result.onFailure {
                            dialogoActivo = DialogoEdicion.Info(
                                "Error",
                                it.message ?: "No se pudo actualizar el correo."
                            )
                        }
                    }
                },
                onDismiss = { dialogoActivo = DialogoEdicion.Ninguno }
            )
        }

        is DialogoEdicion.Password -> {
            DialogoEditarPassword(
                onGuardar = { nuevaPassword ->
                    scope.launch {
                        if (nuevaPassword.length < 6) {
                            dialogoActivo = DialogoEdicion.Info(
                                "Contraseña muy corta",
                                "La contraseña debe tener al menos 6 caracteres."
                            )
                            return@launch
                        }
                        val token = SesionLocal.obtenerAccessToken(context)

                        val result = SupabaseApi.actualizarPassword(
                            accessToken = token,
                            nuevaPassword = nuevaPassword
                        )
                        result.onSuccess {
                            dialogoActivo = DialogoEdicion.Info(
                                "Contraseña actualizada",
                                "Tu contraseña se cambió correctamente."
                            )
                        }
                        result.onFailure {
                            dialogoActivo = DialogoEdicion.Info(
                                "Error",
                                it.message ?: "No se pudo cambiar la contraseña."
                            )
                        }
                    }
                },
                onDismiss = { dialogoActivo = DialogoEdicion.Ninguno }
            )
        }

        is DialogoEdicion.Info -> {
            AlertDialog(
                onDismissRequest = { dialogoActivo = DialogoEdicion.Ninguno },
                title = { Text(dialogo.titulo, color = Color.White) },
                text = { Text(dialogo.mensaje, color = Color.LightGray) },
                confirmButton = {
                    TextButton(onClick = { dialogoActivo = DialogoEdicion.Ninguno }) {
                        Text("Aceptar", color = Color(0xFFB388FF))
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }

        is DialogoEdicion.Ninguno -> { /* nada */ }
    }

    // ── UI principal ─────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
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
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
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
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto de perfil",
                            tint = Color.White,
                            modifier = Modifier.size(88.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = nombreCompleto, color = Color.White, fontSize = 25.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "$tipoUsuario FixNear", color = Color.LightGray, fontSize = 15.sp)

                if (errorCarga.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorCarga, color = Color(0xFFFF6B6B), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    Surface(shape = RoundedCornerShape(50.dp), color = Color(0xFF334155)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "4.97", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(shape = RoundedCornerShape(50.dp), color = Color(0xFF334155)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = if (cuentaVerificada) Color(0xFF38BDF8) else Color.LightGray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (cuentaVerificada) "Verificada" else "No verificada",
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Sección nombre y apellido ────────────────────────────────
                PerfilSeccion {
                    PerfilFila(
                        titulo = "Nombre",
                        valor = nombres.ifBlank { nombreCompleto }
                    ) { dialogoActivo = DialogoEdicion.Nombre }

                    PerfilFila(
                        titulo = "Apellido",
                        valor = apellidos.ifBlank { "Sin apellido" }
                    ) { dialogoActivo = DialogoEdicion.Apellido }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Sección cuenta ───────────────────────────────────────────
                PerfilSeccion {
                    PerfilFila(titulo = "Número de teléfono", valor = telefono) {
                        dialogoActivo = DialogoEdicion.Info(
                            "Editar teléfono",
                            "Para cambiar tu número de teléfono contacta al soporte."
                        )
                    }

                    PerfilFila(titulo = "Correo electrónico", valor = correo) {
                        dialogoActivo = DialogoEdicion.Correo
                    }

                    PerfilFila(titulo = "Tipo de usuario", valor = tipoUsuario) {
                        dialogoActivo = DialogoEdicion.Info(
                            "Tipo de usuario",
                            "Tu tipo de usuario es: $tipoUsuario"
                        )
                    }

                    PerfilFila(titulo = "CURP", valor = "Sin verificar") {
                        dialogoActivo = DialogoEdicion.Info(
                            "Verificar CURP",
                            "Funcionalidad próximamente disponible."
                        )
                    }

                    PerfilFila(titulo = "Cambiar contraseña", valor = "") {
                        dialogoActivo = DialogoEdicion.Password
                    }

                    PerfilFila(titulo = "Llave de acceso", valor = "No creada") {
                        dialogoActivo = DialogoEdicion.Info(
                            "Llave de acceso",
                            "Funcionalidad próximamente disponible."
                        )
                    }

                    PerfilFila(titulo = "Mis redes sociales", valor = "") {
                        dialogoActivo = DialogoEdicion.Info(
                            "Redes sociales",
                            "Funcionalidad próximamente disponible."
                        )
                    }

                    PerfilFila(titulo = "Mis dispositivos", valor = "") {
                        dialogoActivo = DialogoEdicion.Info(
                            "Dispositivos vinculados",
                            "Funcionalidad próximamente disponible."
                        )
                    }

                    PerfilFila(titulo = "Servicios solicitados", valor = "4") {
                        dialogoActivo = DialogoEdicion.Info(
                            "Historial de servicios",
                            "Funcionalidad próximamente disponible."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        SesionLocal.cerrarSesion(context)
                        onCerrarSesion()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Cerrar sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        dialogoActivo = DialogoEdicion.Info(
                            "Eliminar mi cuenta",
                            "Para eliminar tu cuenta de forma permanente contacta al soporte de ClickWork."
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar mi cuenta")
                }

                Spacer(modifier = Modifier.height(35.dp))
            }
        }
    }
}

// ─── Diálogo genérico para editar texto ─────────────────────────────────────
@Composable
private fun DialogoEditarTexto(
    titulo: String,
    etiqueta: String,
    valorInicial: String,
    onGuardar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var valor by remember { mutableStateOf(valorInicial) }
    var guardando by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!guardando) onDismiss() },
        title = { Text(titulo, color = Color.White) },
        text = {
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text(etiqueta, color = Color.LightGray) },
                singleLine = true,
                enabled = !guardando,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF8A2BE2),
                    unfocusedBorderColor = Color(0xFF475569),
                    cursorColor = Color(0xFF8A2BE2),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A)
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (valor.isNotBlank() && !guardando) {
                        guardando = true
                        onGuardar(valor.trim())
                    }
                },
                enabled = !guardando && valor.isNotBlank()
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFFB388FF),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar", color = Color(0xFFB388FF))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!guardando) onDismiss() }) {
                Text("Cancelar", color = Color.LightGray)
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}

// ─── Diálogo para cambiar contraseña ────────────────────────────────────────
@Composable
private fun DialogoEditarPassword(
    onGuardar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var mostrarNueva by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!guardando) onDismiss() },
        title = { Text("Cambiar contraseña", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = nuevaPassword,
                    onValueChange = { nuevaPassword = it; errorLocal = "" },
                    label = { Text("Nueva contraseña", color = Color.LightGray) },
                    singleLine = true,
                    enabled = !guardando,
                    visualTransformation = if (mostrarNueva)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { mostrarNueva = !mostrarNueva }) {
                            Icon(
                                if (mostrarNueva) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8A2BE2),
                        unfocusedBorderColor = Color(0xFF475569),
                        cursorColor = Color(0xFF8A2BE2),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmar,
                    onValueChange = { confirmar = it; errorLocal = "" },
                    label = { Text("Confirmar contraseña", color = Color.LightGray) },
                    singleLine = true,
                    enabled = !guardando,
                    visualTransformation = if (mostrarConfirmar)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { mostrarConfirmar = !mostrarConfirmar }) {
                            Icon(
                                if (mostrarConfirmar) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8A2BE2),
                        unfocusedBorderColor = Color(0xFF475569),
                        cursorColor = Color(0xFF8A2BE2),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A)
                    )
                )

                if (errorLocal.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorLocal, color = Color(0xFFFF6B6B), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        nuevaPassword.length < 6 -> errorLocal = "Mínimo 6 caracteres"
                        nuevaPassword != confirmar -> errorLocal = "Las contraseñas no coinciden"
                        else -> {
                            guardando = true
                            onGuardar(nuevaPassword)
                        }
                    }
                },
                enabled = !guardando && nuevaPassword.isNotBlank()
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFFB388FF),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar", color = Color(0xFFB388FF))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!guardando) onDismiss() }) {
                Text("Cancelar", color = Color.LightGray)
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}

// ─── Helpers para nombre / apellido ─────────────────────────────────────────
fun obtenerNombresPerfil(nombreCompleto: String): String {
    val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.isEmpty() -> ""
        partes.size >= 3 -> partes.take(2).joinToString(" ")
        else -> partes.first()
    }
}

fun obtenerApellidosPerfil(nombreCompleto: String): String {
    val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.size >= 3 -> partes.drop(2).joinToString(" ")
        partes.size == 2 -> partes.drop(1).joinToString(" ")
        else -> ""
    }
}

// ─── Componentes de UI ───────────────────────────────────────────────────────
@Composable
fun PerfilSeccion(contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF334155))
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = contenido)
    }
}

@Composable
fun PerfilFila(titulo: String, valor: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                Text(text = valor, color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFF475569))
    }
}
