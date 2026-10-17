package com.incanatoapps.retofut.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberSession by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var showRulesDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sessionManager = remember { SessionManager(context) }
    val authState = viewModel.authState

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Guardar token e ID de usuario y navegar inmediatamente
            authState.token?.let { sessionManager.saveAuthToken(it) }
            authState.userId?.let { sessionManager.saveUserId(it) }

            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    val onLoginClick = {
        focusManager.clearFocus()
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            validationError = "Por favor ingresa tu correo electrónico"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            validationError = "Ingresa un correo electrónico válido"
        } else if (password.isBlank()) {
            validationError = "Por favor ingresa tu contraseña"
        } else {
            validationError = null
            viewModel.login(trimmedEmail, password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Header Banner estilo ChocoFulbo ---
        ChocoFulboHeader()

        // --- Contenido del Formulario ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = DarkForestGreen,
                fontSize = 26.sp
            )

            Text(
                text = "Ingresa con tu cuenta para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Banner de error o validación
            val displayedError = validationError ?: (authState as? AuthState.Error)?.message
            if (displayedError != null) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = displayedError,
                            color = Color(0xFFB91C1C),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Si hay error de red o servidor local 10.0.2.2 inaccesible, ofrecer bypass a modo exploración
                        if (authState is AuthState.Error) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "💡 ¿El servidor backend no está corriendo? Ingresar en modo prueba",
                                color = DarkForestGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .clickable {
                                        sessionManager.saveAuthToken("demo_token_chiclayo")
                                        sessionManager.saveUserId(1)
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                    .padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Campo: Correo Electrónico
            Text(
                text = "CORREO ELECTRÓNICO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.8.sp,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (validationError != null) validationError = null
                },
                placeholder = {
                    Text("correo@ejemplo.com", color = Color(0xFF9CA3AF), fontSize = 15.sp)
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = "Correo",
                        tint = Color(0xFF4B5563)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkForestGreen,
                    unfocusedBorderColor = BorderGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = DarkForestGreen
                )
            )

            Spacer(Modifier.height(16.dp))

            // Campo: Contraseña
            Text(
                text = "CONTRASEÑA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.8.sp,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (validationError != null) validationError = null
                },
                placeholder = {
                    Text("•••••••••", color = Color(0xFF9CA3AF), fontSize = 16.sp)
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = "Contraseña",
                        tint = Color(0xFF4B5563)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color(0xFF4B5563)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onLoginClick() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkForestGreen,
                    unfocusedBorderColor = BorderGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = DarkForestGreen
                )
            )

            Spacer(Modifier.height(14.dp))

            // Fila: Mantener sesión & ¿Olvidaste tu contraseña?
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { rememberSession = !rememberSession }
                        .testTag("remember_session_row")
                ) {
                    Checkbox(
                        checked = rememberSession,
                        onCheckedChange = { rememberSession = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ForestGreen,
                            checkmarkColor = Color.White,
                            uncheckedColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("remember_session_checkbox")
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Mantener sesión",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkForestGreen,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { navController.navigate(Screen.ForgotPassword.route) }
                        .testTag("forgot_password_button")
                )
            }

            Spacer(Modifier.height(24.dp))

            // Botón Principal: Ingresar ->
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("login_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = Color.White
                ),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ingresar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Divisor con círculo "o"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BorderGray,
                    thickness = 1.dp
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .border(1.dp, BorderGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "o",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BorderGray,
                    thickness = 1.dp
                )
            }

            Spacer(Modifier.height(18.dp))

            // Botón Secundario: Crear cuenta nueva
            Button(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("register_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightButtonGray,
                    contentColor = TextPrimary
                )
            ) {
                Text(
                    text = "Crear cuenta nueva",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            // Botón Especial: Ingresar como invitado (Modo Expo)
            OutlinedButton(
                onClick = {
                    sessionManager.saveAuthToken("expo_guest_token")
                    sessionManager.saveUserId(1)
                    sessionManager.saveUserName("Piero")
                    sessionManager.savePreferredPosition("Delantero")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("guest_login_button"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, DarkForestGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF0FDF4),
                    contentColor = DarkForestGreen
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.SportsSoccer,
                        contentDescription = null,
                        tint = DarkForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ingresar como invitado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkForestGreen,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // Pie de página: Reglas de la comunidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Al continuar aceptas las ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "reglas de la comunidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkForestGreen,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable { showRulesDialog = true }
                        .testTag("community_rules_link")
                )
            }

            Spacer(Modifier.navigationBarsPadding())
        }
    }

    // Modal de Reglas de la Comunidad
    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.SportsSoccer,
                        contentDescription = null,
                        tint = DarkForestGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Reglas de la Comunidad",
                        fontWeight = FontWeight.Bold,
                        color = DarkForestGreen,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "En ChocoFulbo Chiclayo fomentamos la pasión deportiva con valores:",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    RuleItem("1. Juego Limpio:", "Respeto hacia rivales, árbitros y público asistente en todas las canchas.")
                    RuleItem("2. Puntualidad:", "Estar con tu equipo 15 minutos antes del silbatazo inicial.")
                    RuleItem("3. Compromiso:", "Confirmar asistencia para evitar partidos incompletos.")
                    RuleItem("4. Cero Violencia:", "Tolerancia cero ante agresiones verbales o físicas.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRulesDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = CardWhite
        )
    }
}

@Composable
private fun RuleItem(title: String, desc: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkForestGreen)
        Text(desc, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
private fun ChocoFulboHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 38.dp, bottomEnd = 38.dp))
            .background(DarkForestGreen)
            .drawBehind {
                // Trazos decorativos de cancha y geometría sutil (círculos / arcos elegantes)
                val stroke = Stroke(width = 1.5.dp.toPx())
                val lineColor = Color.White.copy(alpha = 0.08f)

                // Círculo decorativo superior derecho
                drawCircle(
                    color = lineColor,
                    radius = size.width * 0.45f,
                    center = Offset(size.width * 0.85f, size.height * 0.25f),
                    style = stroke
                )

                // Arco de cancha decorativo inferior
                drawArc(
                    color = lineColor,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.15f, size.height * 0.4f),
                    size = Size(size.width * 0.7f, size.height * 0.8f),
                    style = stroke
                )

                // Rectángulo sutil que simula área técnica
                drawRoundRect(
                    color = lineColor,
                    topLeft = Offset(size.width * 0.12f, size.height * 0.35f),
                    size = Size(size.width * 0.76f, size.height * 0.55f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                    style = stroke
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 18.dp, bottom = 28.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de pelota dentro de círculo verde neón
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(LimeNeon),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.SportsSoccer,
                    contentDescription = "Balón de fútbol",
                    tint = DarkForestGreen,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Título de la App
            Text(
                text = "ChocoFulbo",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.5.sp,
                fontSize = 32.sp
            )

            Spacer(Modifier.height(8.dp))

            // Píldora / Badge: CHICLAYO AMATEUR
            Surface(
                color = LimeNeon,
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = "CHICLAYO AMATEUR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Subtítulo
            Text(
                text = "Organiza tus partidos, equipos y estadísticas en un solo lugar.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
