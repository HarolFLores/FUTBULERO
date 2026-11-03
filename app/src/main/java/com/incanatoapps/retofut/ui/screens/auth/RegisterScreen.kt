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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.outlined.*
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
import com.incanatoapps.retofut.data.network.model.UserRole
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
    var selectedRole by remember { mutableStateOf(UserRole.JUGADOR_LIBRE) }
    var selectedPosition by remember { mutableStateOf("Delantero") }

    // Campos para Administrador de Cancha
    var complexName by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("Chiclayo Centro") }
    var complexAddress by remember { mutableStateOf("") }
    var complexRuc by remember { mutableStateOf("") }
    var courtCount by remember { mutableIntStateOf(2) }
    var selectedFormats by remember { mutableStateOf(setOf("Fútbol 7", "Fútbol 6")) }

    // Campos para Árbitro Colegiado CODAR
    var refereeLicense by remember { mutableStateOf("") }
    var refereeCategory by remember { mutableStateOf("Primera Categoría FPF") }
    var refereeFee by remember { mutableDoubleStateOf(35.0) }
    var refereeSpecialty by remember { mutableStateOf("Fútbol 7 & 8 Grass Sintético") }

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
            sessionManager.saveUserRole(selectedRole)

            when (selectedRole) {
                UserRole.JUGADOR_LIBRE -> {
                    sessionManager.savePreferredPosition(selectedPosition)
                }
                UserRole.ADMIN_CANCHA -> {
                    sessionManager.saveCourtAdminDetails(
                        complexName = complexName.ifBlank { "Complejo Deportivo El Oasis" },
                        district = selectedDistrict,
                        address = complexAddress.ifBlank { "Av. Los Tréboles 450, Santa Victoria" },
                        ruc = complexRuc.ifBlank { "20608765432" },
                        courtsCount = courtCount,
                        formats = selectedFormats.joinToString(", ")
                    )
                }
                UserRole.ARBITRO -> {
                    sessionManager.saveRefereeDetails(
                        license = refereeLicense.ifBlank { "CODAR-LAMB-#4829" },
                        category = refereeCategory,
                        fee = refereeFee,
                        specialty = refereeSpecialty
                    )
                }
                else -> {}
            }

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
        } else if (selectedRole == UserRole.ADMIN_CANCHA && complexName.trim().isBlank()) {
            validationError = "Ingresa el nombre de tu complejo deportivo"
        } else if (selectedRole == UserRole.ADMIN_CANCHA && complexAddress.trim().isBlank()) {
            validationError = "Ingresa la dirección exacta de tu complejo deportivo"
        } else if (selectedRole == UserRole.ADMIN_CANCHA && complexRuc.trim().length < 8) {
            validationError = "Ingresa el RUC o DNI del administrador (mínimo 8 dígitos)"
        } else if (selectedRole == UserRole.ARBITRO && refereeLicense.trim().isBlank()) {
            validationError = "Ingresa tu Nº de Carnet o Licencia CODAR / FPF"
        } else if (!acceptedTerms) {
            validationError = "Debes aceptar las reglas de la comunidad y términos"
        } else {
            validationError = null
            sessionManager.saveUserName(trimmedName)
            sessionManager.saveUserRole(selectedRole)

            when (selectedRole) {
                UserRole.JUGADOR_LIBRE -> {
                    sessionManager.savePreferredPosition(selectedPosition)
                }
                UserRole.ADMIN_CANCHA -> {
                    sessionManager.saveCourtAdminDetails(
                        complexName = complexName.trim(),
                        district = selectedDistrict,
                        address = complexAddress.trim(),
                        ruc = complexRuc.trim(),
                        courtsCount = courtCount,
                        formats = selectedFormats.joinToString(", ")
                    )
                }
                UserRole.ARBITRO -> {
                    sessionManager.saveRefereeDetails(
                        license = refereeLicense.trim(),
                        category = refereeCategory,
                        fee = refereeFee,
                        specialty = refereeSpecialty
                    )
                }
                else -> {}
            }

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
                                        sessionManager.saveUserRole(selectedRole)
                                        when (selectedRole) {
                                            UserRole.JUGADOR_LIBRE -> {
                                                sessionManager.savePreferredPosition(selectedPosition)
                                            }
                                            UserRole.ADMIN_CANCHA -> {
                                                sessionManager.saveCourtAdminDetails(
                                                    complexName = complexName.ifBlank { "Complejo Deportivo El Oasis" },
                                                    district = selectedDistrict,
                                                    address = complexAddress.ifBlank { "Av. Los Tréboles 450, Santa Victoria" },
                                                    ruc = complexRuc.ifBlank { "20608765432" },
                                                    courtsCount = courtCount,
                                                    formats = selectedFormats.joinToString(", ")
                                                )
                                            }
                                            UserRole.ARBITRO -> {
                                                sessionManager.saveRefereeDetails(
                                                    license = refereeLicense.ifBlank { "CODAR-LAMB-#4829" },
                                                    category = refereeCategory,
                                                    fee = refereeFee,
                                                    specialty = refereeSpecialty
                                                )
                                            }
                                            else -> {}
                                        }
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

            Spacer(Modifier.height(18.dp))

            // Selector: ¿CUÁL ES TU ROL EN CHOCOFULBO?
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormLabel("¿CUÁL ES TU ROL EN CHOCOFULBO?", modifier = Modifier.padding(bottom = 0.dp))
                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "3 ROLES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 3 roles disponibles (Capitán no se elige en registro, se obtiene al fundar equipo o por cesión)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RoleSelectionCard(
                    role = UserRole.JUGADOR_LIBRE,
                    title = "Jugador / Pichanguero",
                    subtitle = "Únete a pichangas con cupos, califica rivales y sube tu OVR FIFA",
                    badge = "POR DEFECTO",
                    badgeColor = Color(0xFFDCFCE7),
                    badgeTextColor = Color(0xFF15803D),
                    isSelected = selectedRole == UserRole.JUGADOR_LIBRE,
                    onClick = { selectedRole = UserRole.JUGADOR_LIBRE }
                )
                RoleSelectionCard(
                    role = UserRole.ADMIN_CANCHA,
                    title = "Dueño / Administrador de Cancha",
                    subtitle = "Gestiona tu complejo deportivo, turnos (08:00 a 23:00) y tarifas",
                    badge = "COMPLEJOS",
                    badgeColor = Color(0xFFFEE2E2),
                    badgeTextColor = Color(0xFF991B1B),
                    isSelected = selectedRole == UserRole.ADMIN_CANCHA,
                    onClick = { selectedRole = UserRole.ADMIN_CANCHA }
                )
                RoleSelectionCard(
                    role = UserRole.ARBITRO,
                    title = "Árbitro Colegiado CODAR",
                    subtitle = "Dirige partidos oficiales, valida actas y fija tarifa por partido",
                    badge = "OFICIAL FPF",
                    badgeColor = Color(0xFFF3E8FF),
                    badgeTextColor = Color(0xFF6B21A8),
                    isSelected = selectedRole == UserRole.ARBITRO,
                    onClick = { selectedRole = UserRole.ARBITRO }
                )
            }

            // Nota informativa sobre cómo convertirse en Capitán
            Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡 ¿Quieres ser Capitán? Todo usuario inicia como Jugador. Al fundar tu propio equipo o al recibir la cinta de capitán de un compañero, tu rol ascenderá automáticamente a Capitán 👑.",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF92400E),
                        lineHeight = 16.sp
                    )
                }
            }

            // ========================================================
            // CAMPOS CONDICIONALES SEGÚN EL ROL SELECCIONADO
            // ========================================================

            // 1. ROL: JUGADOR LIBRE -> Posición Preferida
            if (selectedRole == UserRole.JUGADOR_LIBRE) {
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
            }

            // 2. ROL: ADMINISTRADOR DE CANCHA -> Datos estrictos del Complejo Deportivo
            if (selectedRole == UserRole.ADMIN_CANCHA) {
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, Color(0xFFFECDD3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🏟️", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "DATOS DEL COMPLEJO DEPORTIVO",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    "Información para verificar y listar tus canchas",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    "OBLIGATORIO",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF991B1B),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Nombre del Complejo
                        FormLabel("NOMBRE DEL COMPLEJO / SEDE")
                        OutlinedTextField(
                            value = complexName,
                            onValueChange = { complexName = it },
                            placeholder = { Text("Ej: Complejo Deportivo El Oasis") },
                            leadingIcon = { Icon(Icons.Outlined.Stadium, null, tint = Color(0xFF4B5563)) },
                            modifier = Modifier.fillMaxWidth().testTag("court_complex_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedColors()
                        )

                        Spacer(Modifier.height(10.dp))

                        // Distrito en Chiclayo
                        FormLabel("DISTRITO EN CHICLAYO")
                        val chiclayoDistricts = listOf("Chiclayo Centro", "La Victoria", "Pimentel", "Patazca", "J. L. Ortiz")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            chiclayoDistricts.take(3).forEach { dist ->
                                val isSelected = selectedDistrict == dist
                                Surface(
                                    onClick = { selectedDistrict = dist },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) DarkForestGreen else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = dist,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) LimeNeon else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            chiclayoDistricts.drop(3).forEach { dist ->
                                val isSelected = selectedDistrict == dist
                                Surface(
                                    onClick = { selectedDistrict = dist },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) DarkForestGreen else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = dist,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) LimeNeon else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Dirección exacta
                        FormLabel("DIRECCIÓN EXACTA DE LA SEDE")
                        OutlinedTextField(
                            value = complexAddress,
                            onValueChange = { complexAddress = it },
                            placeholder = { Text("Ej: Av. Los Tréboles 450, Santa Victoria") },
                            leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFF4B5563)) },
                            modifier = Modifier.fillMaxWidth().testTag("court_address_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedColors()
                        )

                        Spacer(Modifier.height(10.dp))

                        // RUC / DNI del Administrador
                        FormLabel("RUC O DNI DEL TITULAR / ADMINISTRADOR")
                        OutlinedTextField(
                            value = complexRuc,
                            onValueChange = { if (it.length <= 11 && it.all { c -> c.isDigit() }) complexRuc = it },
                            placeholder = { Text("RUC 11 dígitos o DNI 8 dígitos") },
                            leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = Color(0xFF4B5563)) },
                            modifier = Modifier.fillMaxWidth().testTag("court_ruc_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = outlinedColors()
                        )

                        Spacer(Modifier.height(10.dp))

                        // Número de Canchas Sintéticas
                        FormLabel("CANTIDAD DE CANCHAS SINTÉTICAS DISPONIBLES")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 4, 5).forEach { count ->
                                val isSelected = courtCount == count
                                Surface(
                                    onClick = { courtCount = count },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF991B1B) else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF991B1B) else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (count == 5) "5+" else "$count",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Formatos Habilitados
                        FormLabel("FORMATOS QUE MANEJA TU COMPLEJO")
                        val formatsList = listOf("Fútbol 5", "Fútbol 6", "Fútbol 7", "Fútbol 8", "Fútbol 11")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            formatsList.forEach { fmt ->
                                val isIncluded = selectedFormats.contains(fmt)
                                Surface(
                                    onClick = {
                                        selectedFormats = if (isIncluded) {
                                            if (selectedFormats.size > 1) selectedFormats - fmt else selectedFormats
                                        } else {
                                            selectedFormats + fmt
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isIncluded) Color(0xFF991B1B) else Color.White,
                                    border = BorderStroke(1.dp, if (isIncluded) Color(0xFF991B1B) else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = fmt.replace("Fútbol ", "F"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isIncluded) Color.White else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. ROL: ÁRBITRO COLEGIADO -> Acreditación y tarifas CODAR Lambayeque
            if (selectedRole == UserRole.ARBITRO) {
                Surface(
                    color = Color(0xFFFAF5FF),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, Color(0xFFE9D5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("⚖️", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "ACREDITACIÓN ARBITRAL CODAR",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B21A8)
                                )
                                Text(
                                    "Federación y tarifas para dirigir en Chiclayo",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Surface(color = Color(0xFFF3E8FF), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    "OFICIAL FPF",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF6B21A8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Nº Carnet / Licencia CODAR
                        FormLabel("Nº DE CARNET / LICENCIA CODAR LAMBAYEQUE")
                        OutlinedTextField(
                            value = refereeLicense,
                            onValueChange = { refereeLicense = it },
                            placeholder = { Text("Ej: CODAR-LAMB-2025-0489") },
                            leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = Color(0xFF4B5563)) },
                            modifier = Modifier.fillMaxWidth().testTag("referee_license_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedColors()
                        )

                        Spacer(Modifier.height(10.dp))

                        // Categoría Arbitral
                        FormLabel("CATEGORÍA ARBITRAL FPF")
                        val categories = listOf("Primera Categoría FPF", "Segunda Categoría", "Aspirante")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = refereeCategory == cat
                                Surface(
                                    onClick = { refereeCategory = cat },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF6B21A8) else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF6B21A8) else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = cat.replace(" Categoría", "").replace(" FPF", ""),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Tarifa por Partido
                        FormLabel("TARIFA POR PARTIDO (EN SOLES)")
                        val fees = listOf(30.0, 35.0, 40.0, 50.0)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            fees.forEach { fee ->
                                val isSelected = refereeFee == fee
                                Surface(
                                    onClick = { refereeFee = fee },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF6B21A8) else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF6B21A8) else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "S/ ${fee.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Especialidad
                        FormLabel("ESPECIALIDAD DE PARTIDOS")
                        val specialties = listOf("Fútbol 7 & 8 Grass Sintético", "Fútbol 6 Rápido", "Fútbol 11 Oficial")
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            specialties.forEach { spec ->
                                val isSelected = refereeSpecialty == spec
                                Surface(
                                    onClick = { refereeSpecialty = spec },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF6B21A8) else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF6B21A8) else BorderGray),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = spec,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Text("✓", color = LimeNeon, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "ℹ️ Como colegiado, podrás dirigir partidos y además participar como jugador en pichangas cambiando a 'Modo Jugador' desde tu perfil.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF6B21A8),
                            lineHeight = 14.sp
                        )
                    }
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
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold, color = Color.White)
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
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold, color = Color.White)
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
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("¡Listo!", fontWeight = FontWeight.Bold, color = Color.White)
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
private fun RoleSelectionCard(
    role: UserRole,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerBg = if (isSelected) Color(0xFFF0FDF4) else Color.White
    val borderStroke = if (isSelected) BorderStroke(1.8.dp, DarkForestGreen) else BorderStroke(1.dp, BorderGray)

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = containerBg,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) LimeNeon else Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (role) {
                        UserRole.CAPITAN -> "👑"
                        UserRole.JUGADOR_LIBRE -> "⚽"
                        UserRole.ADMIN_CANCHA -> "🏟️"
                        UserRole.ARBITRO -> "⚖️"
                        else -> "👤"
                    },
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DarkForestGreen else TextPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Surface(color = badgeColor, shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = DarkForestGreen,
                    unselectedColor = Color(0xFF9CA3AF)
                ),
                modifier = Modifier.size(24.dp)
            )
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
