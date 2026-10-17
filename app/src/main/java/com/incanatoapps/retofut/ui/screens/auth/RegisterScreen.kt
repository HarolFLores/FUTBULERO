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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf("Delantero") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    var showRulesDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sessionManager = remember { SessionManager(context) }
    val authState = viewModel.authState

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authState.token?.let { sessionManager.saveAuthToken(it) }
            authState.userId?.let { sessionManager.saveUserId(it) }
            sessionManager.saveUserName(name.trim())
            sessionManager.savePreferredPosition(selectedPosition)

            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    val onRegisterSubmit = {
        focusManager.clearFocus()
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val cleanPhone = phone.trim()

        if (trimmedName.isBlank()) {
            validationError = "Por favor ingresa tu nombre completo"
        } else if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            validationError = "Ingresa un correo electrónico válido"
        } else if (cleanPhone.isBlank() || cleanPhone.length < 9) {
            validationError = "Ingresa tu número de WhatsApp válido (9 dígitos)"
        } else if (password.isBlank() || password.length < 6) {
            validationError = "La contraseña debe tener al menos 6 caracteres"
        } else if (!acceptedTerms) {
            validationError = "Debes aceptar las reglas de la comunidad y términos"
        } else {
            validationError = null
            sessionManager.saveUserName(trimmedName)
            sessionManager.savePreferredPosition(selectedPosition)
            val fullPhone = "+51 $cleanPhone"
            viewModel.register(trimmedName, trimmedEmail, fullPhone, password, password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Cabecera ChocoFulbo ---
        RegisterHeader(
            onBackClick = {
                viewModel.resetState()
                navController.popBackStack()
            },
            onHelpClick = { showHelpDialog = true }
        )

        // --- Contenido del Formulario ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Etiqueta Temporada
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LimeNeon)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "TEMPORADA 2025",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                )
            }

            // Título y Subtítulo
            Text(
                text = "Crear cuenta nueva",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = DarkForestGreen,
                fontSize = 26.sp
            )

            Text(
                text = "Completa tus datos para empezar a jugar y armar tus pichangas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Banner de error si existe
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

                        // Acceso rápido en modo demo si el backend local no está levantado
                        if (authState is AuthState.Error) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "💡 ¿El servidor backend no está activo? Continuar en modo prueba",
                                color = DarkForestGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .clickable {
                                        sessionManager.saveAuthToken("demo_token_chiclayo")
                                        sessionManager.saveUserId(1)
                                        sessionManager.saveUserName(name.ifBlank { "Carlos Mendoza" })
                                        sessionManager.savePreferredPosition(selectedPosition)
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

            // Campo: NOMBRE COMPLETO
            FormLabel("NOMBRE COMPLETO")
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (validationError != null) validationError = null
                },
                placeholder = {
                    Text("Ej. Carlos Mendoza", color = Color(0xFF9CA3AF), fontSize = 15.sp)
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Nombre",
                        tint = Color(0xFF4B5563)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_name_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = outlinedColors()
            )

            Spacer(Modifier.height(16.dp))

            // Campo: CORREO ELECTRÓNICO
            FormLabel("CORREO ELECTRÓNICO")
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (validationError != null) validationError = null
                },
                placeholder = {
                    Text("tuemail@ejemplo.com", color = Color(0xFF9CA3AF), fontSize = 15.sp)
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
                    .testTag("register_email_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = outlinedColors()
            )

            Spacer(Modifier.height(16.dp))

            // Campo: NÚMERO DE WHATSAPP / CELULAR
            FormLabel("NÚMERO DE WHATSAPP / CELULAR")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prefijo Perú +51
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .width(86.dp)
                        .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🇵🇪", fontSize = 17.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "+51",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        val digits = it.filter { ch -> ch.isDigit() }.take(9)
                        phone = digits
                        if (validationError != null) validationError = null
                    },
                    placeholder = {
                        Text("987 654 321", color = Color(0xFF9CA3AF), fontSize = 15.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Phone,
                            contentDescription = "Teléfono",
                            tint = Color(0xFF4B5563)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("register_phone_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    colors = outlinedColors()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Selector: POSICIÓN PREFERIDA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormLabel("POSICIÓN PREFERIDA", modifier = Modifier.padding(bottom = 0.dp))
                Text(
                    text = "⚽ Elige tu zona",
                    fontSize = 12.sp,
                    color = ForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))

            // Grilla 2x2 de Posiciones
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PositionCard(
                        title = "Delantero",
                        isSelected = selectedPosition == "Delantero",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPosition = "Delantero" }
                    )
                    PositionCard(
                        title = "Mediocampista",
                        isSelected = selectedPosition == "Mediocampista",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPosition = "Mediocampista" }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PositionCard(
                        title = "Defensa",
                        isSelected = selectedPosition == "Defensa",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPosition = "Defensa" }
                    )
                    PositionCard(
                        title = "Arquero",
                        isSelected = selectedPosition == "Arquero",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPosition = "Arquero" }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Campo: CONTRASEÑA
            FormLabel("CONTRASEÑA")
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (validationError != null) validationError = null
                },
                placeholder = {
                    Text("Mínimo 8 caracteres", color = Color(0xFF9CA3AF), fontSize = 15.sp)
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
                    .testTag("register_password_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onRegisterSubmit() }
                ),
                colors = outlinedColors()
            )

            Spacer(Modifier.height(14.dp))

            // Checkbox: Términos y Reglas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ForestGreen,
                        checkmarkColor = Color.White,
                        uncheckedColor = Color(0xFF9CA3AF)
                    ),
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("accept_terms_checkbox")
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Acepto las ",
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "reglas de la comunidad",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkForestGreen,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { showRulesDialog = true }
                        )
                        Text(
                            text = " y ",
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "términos del torneo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkForestGreen,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { showTermsDialog = true }
                        )
                        Text(
                            text = " de ChocoFulbo Chiclayo.",
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // Botón Principal: Registrarme ->
            Button(
                onClick = onRegisterSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_register_button"),
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
                            text = "Registrarme",
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

            // Separador con "o"
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
                        .size(24.dp)
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

            Spacer(Modifier.height(16.dp))

            // Enlace: ¿Ya tienes una cuenta? Iniciar sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? ",
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Iniciar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .testTag("go_to_login_button")
                )
            }

            Spacer(Modifier.height(28.dp))

            // Nota al pie: Canchas oficiales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Canchas oficiales: Patazca, La Victoria y Pimentel",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "1. Fair Play: Respeto estricto a compañeros, rivales y árbitros.",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        "2. Puntualidad Chiclayo: Presentarse 15 minutos antes de la hora fijada.",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        "3. Convivencia deportiva: Fomentar el compañerismo y la camaradería.",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRulesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }

    // Modal de Términos del Torneo
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    "Términos del Torneo ChocoFulbo",
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "• Las pichangas y campeonatos se disputan en sedes verificadas (Patazca, La Victoria, Pimentel).",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        "• Todo jugador debe estar inscrito con datos reales y número de WhatsApp activo para coordinación.",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        "• Los rankings y valoraciones individuales se calculan con base en el desempeño reportado por los capitanes.",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }

    // Modal de Ayuda
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    "¿Cómo funciona ChocoFulbo?",
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "ChocoFulbo es la plataforma para futbolistas amateur en Chiclayo. Regístrate, elige tu posición favorita y podrás unirte a partidos cerca de ti, medir tu nivel y armar tu propio equipo con amigos.",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("¡Listo!", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }
}

@Composable
private fun FormLabel(text: String, modifier: Modifier = Modifier.padding(bottom = 6.dp)) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        letterSpacing = 0.6.sp,
        fontSize = 12.sp,
        modifier = modifier
    )
}

@Composable
private fun PositionCard(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerBg = if (isSelected) Color(0xFFDCFCE7) else Color.White
    val borderStroke = if (isSelected) BorderStroke(1.8.dp, LimeNeon) else BorderStroke(1.dp, BorderGray)
    val contentColor = if (isSelected) DarkForestGreen else TextPrimary

    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerBg,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.SportsSoccer,
                    contentDescription = null,
                    tint = if (isSelected) ForestGreen else Color(0xFF6B7280),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ForestGreen)
                )
            }
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = DarkForestGreen,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = DarkForestGreen
)

@Composable
private fun RegisterHeader(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(DarkForestGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp, bottom = 18.dp, start = 12.dp, end = 16.dp)
        ) {
            // Barra superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }

                // Círculo con balón
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(LimeNeon),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.SportsSoccer,
                        contentDescription = null,
                        tint = DarkForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Marca ChocoFulbo
                Text(
                    text = "ChocoFulbo",
                    fontWeight = FontWeight.Black,
                    color = LimeNeon,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.width(8.dp))

                // Píldora CHICLAYO AMATEUR
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, LimeNeon.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = "CHICLAYO\nAMATEUR",
                        color = LimeNeon,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 9.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                // Botón Ayuda
                IconButton(onClick = onHelpClick) {
                    Icon(
                        Icons.Outlined.HelpOutline,
                        contentDescription = "Ayuda",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Subtítulo central
            Text(
                text = "Únete a la mayor comunidad de fútbol amateur de Chiclayo",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}
