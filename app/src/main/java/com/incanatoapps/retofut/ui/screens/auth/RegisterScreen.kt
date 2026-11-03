package com.incanatoapps.retofut.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    // Control de paso del wizard (1: Identidad, 2: Selección de Rol, 3: Ficha Técnica)
    var currentStep by remember { mutableIntStateOf(1) }

    // ================= PASO 1: Identidad ChocoFulbo =================
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ================= PASO 2: Selección de Rol =================
    // Capitán no se selecciona: se gana al crear equipo o por cesión de cinta
    var selectedRole by remember { mutableStateOf(UserRole.JUGADOR_LIBRE) }

    // ================= PASO 3: Ficha Técnica =================
    // Tab activo en Paso 3 (Ficha Jugador, Datos Cancha, Acreditación Árbitro)
    var step3ActiveTab by remember { mutableIntStateOf(0) }

    // Sincronizar el tab activo según el rol seleccionado al entrar al Paso 3
    LaunchedEffect(currentStep, selectedRole) {
        if (currentStep == 3) {
            step3ActiveTab = when (selectedRole) {
                UserRole.JUGADOR_LIBRE, UserRole.CAPITAN -> 0
                UserRole.ADMIN_CANCHA -> 1
                UserRole.ARBITRO -> 2
                else -> 0
            }
        }
    }

    // Datos de Ficha Jugador
    var selectedPosition by remember { mutableStateOf("Delantero") }
    var preferredFoot by remember { mutableStateOf("Derecha") }
    var playerNickname by remember { mutableStateOf("") } // Opcional ("El Rayo de Patazca")

    // Datos de Dueño de Cancha
    var complexName by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("Chiclayo Centro") }
    var complexAddress by remember { mutableStateOf("") }
    var complexRuc by remember { mutableStateOf("") }
    var courtCount by remember { mutableIntStateOf(2) }
    var selectedFormats by remember { mutableStateOf(setOf("Fútbol 7", "Fútbol 6")) }
    var complexMapsUrl by remember { mutableStateOf("https://maps.google.com/?q=Complejo+Deportivo+El+Oasis+Chiclayo") }
    var selectedPhotos by remember { mutableStateOf(setOf("Césped Sintético Pro 50mm", "Iluminación Nocturna LED")) }
    var hasModularF11 by remember { mutableStateOf(true) }
    var priceDayF7 by remember { mutableDoubleStateOf(70.0) }
    var priceNightF7 by remember { mutableDoubleStateOf(110.0) }
    var priceDayF11 by remember { mutableDoubleStateOf(180.0) }
    var priceNightF11 by remember { mutableDoubleStateOf(260.0) }
    var hasVipCourt by remember { mutableStateOf(true) }
    var vipCourtName by remember { mutableStateOf("Cancha 1 Techada LED") }
    var vipCourtPrice by remember { mutableDoubleStateOf(130.0) }

    // Datos de Árbitro Colegiado CODAR
    var refereeLicense by remember { mutableStateOf("") }
    var refereeCategory by remember { mutableStateOf("Primera Categoría FPF") }
    var refereeFee by remember { mutableDoubleStateOf(35.0) }
    var refereeSpecialty by remember { mutableStateOf("Fútbol 7 & 8 Grass Sintético") }

    // Términos y Errores
    var acceptedTerms by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    var showRulesDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sessionManager = remember { SessionManager(context) }
    val authState = viewModel.authState
    val scrollState = rememberScrollState()

    // Retroceso por botón físico / gesto nativo
    BackHandler {
        if (currentStep > 1) {
            validationError = null
            currentStep--
        } else {
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    // Navegación tras login/registro exitoso
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authState.token?.let { sessionManager.saveAuthToken(it) }
            authState.userId?.let { sessionManager.saveUserId(it) }
            sessionManager.saveUserName(name.trim().ifBlank { "Christian Cueva" })
            sessionManager.saveUserRole(selectedRole)

            when (selectedRole) {
                UserRole.JUGADOR_LIBRE, UserRole.CAPITAN -> {
                    sessionManager.savePreferredPosition(selectedPosition)
                    sessionManager.savePlayerFoot(preferredFoot)
                    if (playerNickname.isNotBlank()) {
                        sessionManager.savePlayerNickname(playerNickname.trim())
                    }
                }
                UserRole.ADMIN_CANCHA -> {
                    sessionManager.saveCourtAdminDetails(
                        complexName = complexName.ifBlank { "Complejo Deportivo El Oasis" },
                        district = selectedDistrict,
                        address = complexAddress.ifBlank { "Av. Los Tréboles 450, Santa Victoria" },
                        ruc = complexRuc.ifBlank { "20608765432" },
                        courtsCount = courtCount,
                        formats = selectedFormats.joinToString(", "),
                        mapsUrl = complexMapsUrl,
                        imageUri = selectedPhotos.joinToString(", "),
                        hasModularF11 = hasModularF11,
                        priceDayF7 = priceDayF7,
                        priceNightF7 = priceNightF7,
                        priceDayF11 = priceDayF11,
                        priceNightF11 = priceNightF11,
                        specialVipCourtName = if (hasVipCourt) vipCourtName else "",
                        specialVipPrice = if (hasVipCourt) vipCourtPrice else 0.0
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

    // Validación estricta para Paso 1
    val validateStep1: () -> Boolean = {
        focusManager.clearFocus()
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val cleanPhone = phone.trim()

        if (trimmedName.isBlank()) {
            validationError = "Por favor ingresa tus nombres y apellidos"
            false
        } else if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            validationError = "Ingresa un correo electrónico válido"
            false
        } else if (cleanPhone.isBlank() || cleanPhone.length < 9) {
            validationError = "Ingresa tu número de WhatsApp de Chiclayo (9 dígitos)"
            false
        } else if (password.isBlank() || password.length < 6) {
            validationError = "La contraseña debe tener al menos 6 caracteres"
            false
        } else {
            validationError = null
            true
        }
    }

    // Validación final y envío en Paso 3
    // ¡Los datos no correspondientes al rol NO tienen que ser ingresados!
    val onRegisterSubmit = {
        focusManager.clearFocus()
        val trimmedName = name.trim().ifBlank { "Christian Cueva" }
        val trimmedEmail = email.trim().ifBlank { "aladino.cix@gmail.com" }
        val cleanPhone = phone.trim().ifBlank { "974812340" }

        var hasError = false
        if (selectedRole == UserRole.ADMIN_CANCHA && complexName.trim().isBlank()) {
            validationError = "Ingresa el nombre del complejo deportivo"
            hasError = true
        } else if (selectedRole == UserRole.ADMIN_CANCHA && complexAddress.trim().isBlank()) {
            validationError = "Ingresa la dirección de tu complejo"
            hasError = true
        } else if (selectedRole == UserRole.ARBITRO && refereeLicense.trim().isBlank()) {
            validationError = "Ingresa tu carnet o licencia CODAR"
            hasError = true
        } else if (!acceptedTerms) {
            validationError = "Debes aceptar los términos y el código de juego limpio"
            hasError = true
        }

        if (!hasError) {
            validationError = null
            sessionManager.saveUserName(trimmedName)
            sessionManager.saveUserRole(selectedRole)

            when (selectedRole) {
                UserRole.JUGADOR_LIBRE, UserRole.CAPITAN -> {
                    sessionManager.savePreferredPosition(selectedPosition)
                    sessionManager.savePlayerFoot(preferredFoot)
                    if (playerNickname.isNotBlank()) {
                        sessionManager.savePlayerNickname(playerNickname.trim())
                    }
                }
                UserRole.ADMIN_CANCHA -> {
                    sessionManager.saveCourtAdminDetails(
                        complexName = complexName.trim(),
                        district = selectedDistrict,
                        address = complexAddress.trim(),
                        ruc = complexRuc.trim().ifBlank { "20608765432" },
                        courtsCount = courtCount,
                        formats = selectedFormats.joinToString(", "),
                        mapsUrl = complexMapsUrl,
                        imageUri = selectedPhotos.joinToString(", "),
                        hasModularF11 = hasModularF11,
                        priceDayF7 = priceDayF7,
                        priceNightF7 = priceNightF7,
                        priceDayF11 = priceDayF11,
                        priceNightF11 = priceNightF11,
                        specialVipCourtName = if (hasVipCourt) vipCourtName else "",
                        specialVipPrice = if (hasVipCourt) vipCourtPrice else 0.0
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

    // Fondo gris muy claro para contrastar con las tarjetas blancas
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8F9))
            .verticalScroll(scrollState)
    ) {
        // ============================================================
        // 1. CABECERA VERDE OSCURO (Banner ChocoFulbo Chiclayo)
        // ============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkForestGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                validationError = null
                                currentStep--
                            } else {
                                viewModel.resetState()
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        Text(
                            text = "CHOCOFULBO CHICLAYO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = LimeNeon,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Datos Del Delegado",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            contentDescription = "Ayuda",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Outlined.PersonOutline,
                            contentDescription = "Cuenta",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Sub-Banner informativo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(LimeNeon)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "TEMPORADA CHICLAYO 2025",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LimeNeon,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Fútbol Amateur Norteño",
                        fontSize = 10.5.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Registro de Nuevo Talento",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "Únete a la mayor red de pichangas, ligas barriales y complejos deportivos de Chiclayo.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 22.dp)
                )
            }
        }

        // ============================================================
        // 2. LÍNEA DE TIEMPO / STEPPER FLOTANTE (Overlapping Card)
        // ============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-18).dp)
        ) {
            RegistrationTimelineCard(currentStep = currentStep)
        }

        // ============================================================
        // 3. CONTENIDO DEL FORMULARIO DINÁMICO
        // ============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-10).dp)
        ) {
            // Banner de error o alerta
            val displayedError = validationError ?: (authState as? AuthState.Error)?.message
            if (displayedError != null) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = displayedError,
                            color = Color(0xFFB91C1C),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Modo prueba si el backend local no está levantado
                        if (authState is AuthState.Error) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "💡 ¿Servidor backend inactivo? Continuar en modo prueba local",
                                color = DarkForestGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .clickable {
                                        sessionManager.saveAuthToken("demo_token_chiclayo")
                                        sessionManager.saveUserId(1)
                                        sessionManager.saveUserName(name.ifBlank { "Christian Cueva" })
                                        sessionManager.saveUserRole(selectedRole)
                                        when (selectedRole) {
                                            UserRole.JUGADOR_LIBRE, UserRole.CAPITAN -> {
                                                sessionManager.savePreferredPosition(selectedPosition)
                                                sessionManager.savePlayerFoot(preferredFoot)
                                                if (playerNickname.isNotBlank()) sessionManager.savePlayerNickname(playerNickname)
                                            }
                                            UserRole.ADMIN_CANCHA -> {
                                                sessionManager.saveCourtAdminDetails(
                                                    complexName = complexName.ifBlank { "Complejo Deportivo El Oasis" },
                                                    district = selectedDistrict,
                                                    address = complexAddress.ifBlank { "Av. Los Tréboles 450, Santa Victoria" },
                                                    ruc = complexRuc.ifBlank { "20608765432" },
                                                    courtsCount = courtCount,
                                                    formats = selectedFormats.joinToString(", "),
                                                    mapsUrl = complexMapsUrl,
                                                    imageUri = selectedPhotos.joinToString(", "),
                                                    hasModularF11 = hasModularF11,
                                                    priceDayF7 = priceDayF7,
                                                    priceNightF7 = priceNightF7,
                                                    priceDayF11 = priceDayF11,
                                                    priceNightF11 = priceNightF11,
                                                    specialVipCourtName = if (hasVipCourt) vipCourtName else "",
                                                    specialVipPrice = if (hasVipCourt) vipCourtPrice else 0.0
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

            // Transición suave entre pasos
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "StepTransition"
            ) { step ->
                when (step) {
                    1 -> {
                        // ==========================================
                        // PASO 1: CREA TU IDENTIDAD CHOCOFULBO
                        // ==========================================
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Crea tu identidad ChocoFulbo",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen
                                )
                                Text(
                                    text = "Tus datos servirán para contactarte para partidos y torneos oficiales.",
                                    fontSize = 12.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 3.dp, bottom = 18.dp)
                                )

                                // Nombre y Apellidos
                                FormLabel("Nombre y Apellidos")
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = {
                                        name = it
                                        if (validationError != null) validationError = null
                                    },
                                    placeholder = { Text("Christian Cueva Quispe", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("register_name_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    colors = modernInputColors()
                                )

                                Spacer(Modifier.height(14.dp))

                                // Correo Electrónico
                                FormLabel("Correo Electrónico")
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        if (validationError != null) validationError = null
                                    },
                                    placeholder = { Text("aladino.cix@gmail.com", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Email, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("register_email_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    colors = modernInputColors()
                                )

                                Spacer(Modifier.height(14.dp))

                                // Número de WhatsApp (Chiclayo)
                                FormLabel("Número de WhatsApp (Chiclayo)")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(52.dp)
                                            .width(76.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF9FAFB))
                                            .border(1.dp, BorderGray, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🇵🇪", fontSize = 16.sp)
                                            Spacer(Modifier.width(4.dp))
                                            Text("+51", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimary)
                                        }
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    OutlinedTextField(
                                        value = phone,
                                        onValueChange = {
                                            val digits = it.filter { ch -> ch.isDigit() }.take(9)
                                            phone = digits
                                            if (validationError != null) validationError = null
                                        },
                                        placeholder = { Text("974 812 340", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                                        modifier = Modifier.weight(1f).testTag("register_phone_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                        colors = modernInputColors()
                                    )
                                }

                                Spacer(Modifier.height(14.dp))

                                // Contraseña de acceso
                                FormLabel("Contraseña de acceso")
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        if (validationError != null) validationError = null
                                    },
                                    placeholder = { Text("Mínimo 6 caracteres", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth().testTag("register_password_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { if (validateStep1()) currentStep = 2 }),
                                    colors = modernInputColors()
                                )

                                // Barra de seguridad de contraseña
                                val (strengthLevel, strengthLabel) = calculatePasswordStrength(password)
                                if (password.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            for (i in 1..4) {
                                                val active = strengthLevel >= i
                                                val barColor = when {
                                                    !active -> Color(0xFFE5E7EB)
                                                    strengthLevel <= 1 -> Color(0xFFEF4444)
                                                    strengthLevel == 2 -> Color(0xFFF59E0B)
                                                    strengthLevel == 3 -> Color(0xFF84CC16)
                                                    else -> Color(0xFF16A34A)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(4.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(barColor)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = strengthLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (strengthLevel) {
                                                1 -> Color(0xFFEF4444)
                                                2 -> Color(0xFFD97706)
                                                else -> Color(0xFF15803D)
                                            }
                                        )
                                    }
                                }

                                Spacer(Modifier.height(24.dp))

                                // Botón Continuar al Paso 2: Tu Rol
                                Button(
                                    onClick = {
                                        if (validateStep1()) {
                                            currentStep = 2
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("step1_next_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkForestGreen,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Continuar al Paso 2: Tu Rol",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = LimeNeon,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Enlace de inicio de sesión
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("¿Ya formas parte de una liga? ", fontSize = 13.sp, color = TextPrimary)
                                    Text(
                                        text = "Iniciar sesión",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkForestGreen,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier
                                            .clickable { navController.popBackStack() }
                                            .testTag("go_to_login_button")
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // ==========================================
                        // PASO 2: ¿CÓMO VAS A JUGAR EN CHICLAYO?
                        // ==========================================
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "¿Cómo vas a jugar en Chiclayo?",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                            Text(
                                text = "Selecciona tu perfil principal. Puedes alternar tus funciones dentro de la app en cualquier momento.",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 3.dp, bottom = 14.dp)
                            )

                            // 1. Tarjeta Jugador / Pichanguero
                            RoleOptionCard(
                                icon = "⚽",
                                title = "Jugador / Pichanguero",
                                badgeText = "POPULAR",
                                badgeColor = Color(0xFFDCFCE7),
                                badgeTextColor = Color(0xFF15803D),
                                subtitle = "Para atletas y apasionados del fulbito nocturno",
                                description = "Arma pichangas, busca partidos de último minuto, califica a tus rivales y suma goles a tu tarjeta de jugador oficial.",
                                isSelected = selectedRole == UserRole.JUGADOR_LIBRE,
                                onClick = { selectedRole = UserRole.JUGADOR_LIBRE }
                            )

                            Spacer(Modifier.height(10.dp))

                            // 2. Tarjeta Dueño de Cancha
                            RoleOptionCard(
                                icon = "🏟️",
                                title = "Dueño de Cancha",
                                badgeText = "COMPLEJOS",
                                badgeColor = Color(0xFFF3F4F6),
                                badgeTextColor = Color(0xFF4B5563),
                                subtitle = "Sintéticos F5, F6, F7 y F8",
                                description = "Publica tus canchas, administra horarios en tiempo real, recibe señas por Yape/Plin y llena tus turnos vacíos en Chiclayo.",
                                isSelected = selectedRole == UserRole.ADMIN_CANCHA,
                                onClick = { selectedRole = UserRole.ADMIN_CANCHA }
                            )

                            Spacer(Modifier.height(10.dp))

                            // 3. Tarjeta Árbitro Oficial CODAR
                            RoleOptionCard(
                                icon = "⚖️",
                                title = "Árbitro Oficial CODAR",
                                badgeText = "OFICIAL FPF",
                                badgeColor = Color(0xFFF3F4F6),
                                badgeTextColor = Color(0xFF4B5563),
                                subtitle = "Lambayeque & Torneos Locales",
                                description = "Dirige partidos oficiales, fija tu honorario por partido en soles, y accede al sistema de reportes disciplinarios.",
                                isSelected = selectedRole == UserRole.ARBITRO,
                                onClick = { selectedRole = UserRole.ARBITRO }
                            )

                            Spacer(Modifier.height(14.dp))

                            // Banner Explicativo de Capitán
                            Surface(
                                color = DarkForestGreen,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(LimeNeon),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👑", fontSize = 18.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "¿Quieres ser Capitán de Equipo?",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LimeNeon
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Todo usuario inicia como Jugador. Al fundar tu propio club barrial o recibir la cinta de tu escuadra en ChocoFulbo, tu cuenta ascenderá automáticamente a Capitán de Equipo con panel de fichajes.",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.9f),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            // Botones de Navegación (Atrás y Continuar)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        validationError = null
                                        currentStep = 1
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFE5E7EB),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = DarkForestGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("Atrás", fontWeight = FontWeight.Bold, color = DarkForestGreen, fontSize = 14.sp)
                                    }
                                }

                                Button(
                                    onClick = { currentStep = 3 },
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(50.dp)
                                        .testTag("step2_next_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkForestGreen,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Continuar a Ficha Técnica", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = LimeNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // ==========================================
                        // PASO 3: FICHA TÉCNICA DINÁMICA
                        // ==========================================
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Selector de pestañas para ver las fichas
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE5E7EB))
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val tabs = listOf("Ficha Jugador", "Datos Cancha", "Acreditación Árbitro")
                                tabs.forEachIndexed { index, tabTitle ->
                                    val isSelected = step3ActiveTab == index
                                    Surface(
                                        onClick = { step3ActiveTab = index },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = tabTitle,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) DarkForestGreen else Color(0xFF6B7280),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // PESTAÑA 0: FICHA DE JUGADOR (Posición, Pierna hábil, Apodo opcional)
                            if (step3ActiveTab == 0) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Posición en el Campo",
                                                    fontSize = 17.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = DarkForestGreen
                                                )
                                                Text(
                                                    text = "Selecciona tu demarcación habitual en el fulbito",
                                                    fontSize = 11.5.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(DarkForestGreen),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Outlined.SportsSoccer,
                                                    contentDescription = null,
                                                    tint = LimeNeon,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))

                                        // Grilla 2x2 de posiciones con badges tácticos
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                DetailedPositionCard(
                                                    position = "Delantero",
                                                    tag = "DEL",
                                                    tagColor = Color(0xFF15803D),
                                                    desc = "Puntas, Extremos, Pivotes",
                                                    icon = "⚽",
                                                    isSelected = selectedPosition == "Delantero",
                                                    modifier = Modifier.weight(1f),
                                                    onClick = { selectedPosition = "Delantero" }
                                                )
                                                DetailedPositionCard(
                                                    position = "Mediocampista",
                                                    tag = "MED",
                                                    tagColor = Color(0xFFB45309),
                                                    desc = "Creación, Contención",
                                                    icon = "🎯",
                                                    isSelected = selectedPosition == "Mediocampista",
                                                    modifier = Modifier.weight(1f),
                                                    onClick = { selectedPosition = "Mediocampista" }
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                DetailedPositionCard(
                                                    position = "Defensa",
                                                    tag = "DEF",
                                                    tagColor = Color(0xFF2563EB),
                                                    desc = "Zaguero central, Lateral",
                                                    icon = "🛡️",
                                                    isSelected = selectedPosition == "Defensa",
                                                    modifier = Modifier.weight(1f),
                                                    onClick = { selectedPosition = "Defensa" }
                                                )
                                                DetailedPositionCard(
                                                    position = "Arquero",
                                                    tag = "ARQ",
                                                    tagColor = Color(0xFF7C3AED),
                                                    desc = "Bajo los 3 palos",
                                                    icon = "🧤",
                                                    isSelected = selectedPosition == "Arquero",
                                                    modifier = Modifier.weight(1f),
                                                    onClick = { selectedPosition = "Arquero" }
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        // Pierna Hábil
                                        FormLabel("Pierna Hábil")
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("Derecha", "Izquierda", "Ambidiestro").forEach { foot ->
                                                val isSelected = preferredFoot == foot
                                                Surface(
                                                    onClick = { preferredFoot = foot },
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                                    border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = foot,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) LimeNeon else TextPrimary,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(vertical = 10.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))

                                        // Chapa / Apodo en la Cancha (Opcional)
                                        FormLabel("Chapa / Apodo en la Cancha (Opcional)")
                                        OutlinedTextField(
                                            value = playerNickname,
                                            onValueChange = { playerNickname = it },
                                            placeholder = { Text("Ej: El Rayo de Patazca", color = Color(0xFF9CA3AF), fontSize = 13.5.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Outlined.Flag, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("player_nickname_input"),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = modernInputColors()
                                        )
                                    }
                                }
                            }

                            // PESTAÑA 1: DATOS DE CANCHA (Ficha Oficial del Complejo según Stitch screen_32d45707)
                            if (step3ActiveTab == 1) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // ==========================================
                                    // CARD 1: SEDE Y UBICACIÓN SATELITAL
                                    // ==========================================
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(DarkForestGreen),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Outlined.Stadium, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(20.dp))
                                                }
                                                Spacer(Modifier.width(10.dp))
                                                Column {
                                                    Text("Sede y Ubicación Satelital", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                    Text("Visible para capitanes y árbitros del torneo", fontSize = 11.sp, color = TextSecondary)
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Nombre del Complejo
                                            FormLabel("Nombre de Sede o Complejo")
                                            OutlinedTextField(
                                                value = complexName,
                                                onValueChange = { complexName = it },
                                                placeholder = { Text("Complejo Deportivo El Oasis") },
                                                leadingIcon = { Icon(Icons.Outlined.SportsSoccer, null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp)) },
                                                modifier = Modifier.fillMaxWidth().testTag("court_complex_name_input"),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                colors = modernInputColors()
                                            )

                                            Spacer(Modifier.height(12.dp))

                                            // Distrito en Chiclayo
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                FormLabel("Distrito de Chiclayo")
                                                Text(selectedDistrict, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                            }
                                            val chiclayoDistricts = listOf("Chiclayo Centro", "La Victoria", "Pimentel", "Patazca", "J.L. Ortiz")
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                chiclayoDistricts.take(3).forEach { dist ->
                                                    val isSelected = selectedDistrict == dist
                                                    Surface(
                                                        onClick = { selectedDistrict = dist },
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                                        border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = dist,
                                                            fontSize = 10.5.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
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
                                                        color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                                        border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = dist,
                                                            fontSize = 10.5.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            color = if (isSelected) LimeNeon else TextPrimary,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(vertical = 7.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(12.dp))

                                            // Dirección Física Exacta
                                            FormLabel("Dirección Física Exacta")
                                            OutlinedTextField(
                                                value = complexAddress,
                                                onValueChange = { complexAddress = it },
                                                placeholder = { Text("Av. Las Américas 420, Urb. Santa Victoria") },
                                                leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp)) },
                                                modifier = Modifier.fillMaxWidth().testTag("court_address_input"),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                colors = modernInputColors()
                                            )

                                            Spacer(Modifier.height(12.dp))

                                            // Enlace de Google Maps / Coordenadas
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                FormLabel("Enlace de Google Maps / Coordenadas")
                                                Surface(
                                                    color = LimeNeon,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "GPS RECOMENDADO",
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = DarkForestGreen,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            OutlinedTextField(
                                                value = complexMapsUrl,
                                                onValueChange = { complexMapsUrl = it },
                                                placeholder = { Text("https://maps.app.goo.gl/9X98Z1kOasisCix") },
                                                leadingIcon = { Text("🗺️", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp, end = 6.dp)) },
                                                modifier = Modifier.fillMaxWidth().testTag("court_maps_input"),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                colors = modernInputColors()
                                            )

                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = "🔒 Permite a la hinchada y planteles llegar usando Waze o Google Maps sin desvíos.",
                                                fontSize = 10.5.sp,
                                                color = TextSecondary,
                                                lineHeight = 14.sp
                                            )

                                            Spacer(Modifier.height(12.dp))

                                            // Mini-Mapa Visual de Chiclayo
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(115.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(Color(0xFFE8ECEF))
                                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                                            ) {
                                                // Grilla visual simulada del mapa de Chiclayo
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(10.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("Lambayeque", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                                        Text("Parque Capote", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(Modifier.weight(1f))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(16.dp)
                                                                .clip(CircleShape)
                                                                .background(LimeNeon.copy(alpha = 0.4f)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(8.dp)
                                                                    .clip(CircleShape)
                                                                    .background(DarkForestGreen)
                                                            )
                                                        }
                                                    }
                                                    Spacer(Modifier.weight(1f))
                                                }

                                                // Pill flotante de Geolocalización
                                                Surface(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .padding(bottom = 8.dp),
                                                    color = DarkForestGreen,
                                                    shape = RoundedCornerShape(20.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("✓", color = LimeNeon, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                                        Spacer(Modifier.width(6.dp))
                                                        Text(
                                                            text = "Geolocalización Sincronizada: Chiclayo - Santa Victoria",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // ==========================================
                                    // CARD 2: SERVICIOS DE LA SEDE
                                    // ==========================================
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(DarkForestGreen),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Outlined.Security, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(20.dp))
                                                    }
                                                    Spacer(Modifier.width(10.dp))
                                                    Column {
                                                        Text("Servicios de la Sede", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                        Text("Equipamiento verificado para alquiler", fontSize = 11.sp, color = TextSecondary)
                                                    }
                                                }

                                                Surface(
                                                    color = Color(0xFFDCFCE7),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = "4/4 ACTIVOS",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color(0xFF15803D),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Grilla 2x2 de Servicios en Verde Oscuro
                                            val services = listOf(
                                                Triple("Césped Sintético Pro 50mm", "Monofilamento FIFA Star", "🌱"),
                                                Triple("Iluminación LED 400W", "8 Focos nocturnos", "💡"),
                                                Triple("Vestuarios & Gradas", "Duchas y tribuna 60 pers.", "🚿"),
                                                Triple("Estacionamiento Privado", "Vigilancia en puerta", "🅿️")
                                            )

                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    services.take(2).forEach { (srvTitle, srvDesc, srvEmoji) ->
                                                        Surface(
                                                            modifier = Modifier.weight(1f),
                                                            shape = RoundedCornerShape(14.dp),
                                                            color = DarkForestGreen
                                                        ) {
                                                            Column(modifier = Modifier.padding(12.dp)) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(srvEmoji, fontSize = 18.sp)
                                                                    Text("✓", color = LimeNeon, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                                                }
                                                                Spacer(Modifier.height(6.dp))
                                                                Text(srvTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                                Text(srvDesc, fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                                                            }
                                                        }
                                                    }
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    services.drop(2).forEach { (srvTitle, srvDesc, srvEmoji) ->
                                                        Surface(
                                                            modifier = Modifier.weight(1f),
                                                            shape = RoundedCornerShape(14.dp),
                                                            color = DarkForestGreen
                                                        ) {
                                                            Column(modifier = Modifier.padding(12.dp)) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(srvEmoji, fontSize = 18.sp)
                                                                    Text("✓", color = LimeNeon, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                                                }
                                                                Spacer(Modifier.height(6.dp))
                                                                Text(srvTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                                Text(srvDesc, fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Fotos Reales del Césped y Arcos
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                FormLabel("Fotos Reales del Césped y Arcos")
                                                Text("2 de 3 añadidas", fontSize = 10.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                // Foto 1: Cancha A
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(84.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(Color(0xFF0F3D28)),
                                                    contentAlignment = Alignment.BottomStart
                                                ) {
                                                    Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Text("🏟️", fontSize = 20.sp)
                                                        Surface(
                                                            color = DarkForestGreen.copy(alpha = 0.85f),
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text("Cancha A", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LimeNeon, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                }

                                                // Foto 2: Grama 50mm
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(84.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(Color(0xFF1E5638)),
                                                    contentAlignment = Alignment.BottomStart
                                                ) {
                                                    Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                                        Text("⚽", fontSize = 20.sp)
                                                        Surface(
                                                            color = DarkForestGreen.copy(alpha = 0.85f),
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text("Grama 50mm", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                }

                                                // Foto 3: + Añadir Foto
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(84.dp),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFFF9FAFB),
                                                    border = BorderStroke(1.dp, BorderGray)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxSize(),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                                                        Spacer(Modifier.height(4.dp))
                                                        Text("+ Añadir foto", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                text = "Recomendado: mostrar mallas de arcos, tribunas y tablero marcador si aplica.",
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    // ==========================================
                                    // CARD 3: MODULARIDAD Y CAPACIDAD
                                    // ==========================================
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0xFFDCFCE7)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("📐", fontSize = 18.sp)
                                                }
                                                Spacer(Modifier.width(10.dp))
                                                Column {
                                                    Text("Modularidad y Capacidad", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                    Text("Estructuración táctica de campos de juego", fontSize = 11.sp, color = TextSecondary)
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            FormLabel("Cantidad de Canchas Sintéticas en el Complejo")
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                listOf(1, 2, 3, 4, 5).forEach { count ->
                                                    val isSelected = courtCount == count
                                                    Surface(
                                                        onClick = { courtCount = count },
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                                        border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = if (count == 5) "5+" else "$count",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) LimeNeon else TextPrimary,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(vertical = 9.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Switch Modularidad F11 -> 2x F7
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text("¿Campo F11 divisible en 2x F7?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                                                        Spacer(Modifier.width(6.dp))
                                                        Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                                            Text("MODULAR", fontSize = 8.sp, fontWeight = FontWeight.Black, color = LimeNeon, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                    Spacer(Modifier.height(2.dp))
                                                    Text(
                                                        text = "Permite alquilar el campo completo para torneos grandes o dividirlo automáticamente en Cancha A y Cancha B (F7 simultáneo) en la grilla de turnos de ChocoFulbo.",
                                                        fontSize = 10.5.sp,
                                                        color = TextSecondary,
                                                        lineHeight = 14.sp
                                                    )
                                                }

                                                Switch(
                                                    checked = hasModularF11,
                                                    onCheckedChange = { hasModularF11 = it },
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = ForestGreen
                                                    )
                                                )
                                            }

                                            if (hasModularF11) {
                                                Spacer(Modifier.height(10.dp))
                                                // Diagrama táctico de división Cancha A y B
                                                Surface(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = DarkForestGreen
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Text("CANCHA A (F7)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                                            Text("25m x 45m", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                                                        }

                                                        Text("⇌", color = LimeNeon, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Text("CANCHA B (F7)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                                            Text("25m x 45m", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // ==========================================
                                    // CARD 4: TARIFAS OFICIALES POR HORA
                                    // ==========================================
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(DarkForestGreen),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text("💵", fontSize = 18.sp)
                                                    }
                                                    Spacer(Modifier.width(10.dp))
                                                    Column {
                                                        Text("Tarifas Oficiales por Hora", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                        Text("Configuración en Soles peruanos (S/.)", fontSize = 11.sp, color = TextSecondary)
                                                    }
                                                }

                                                Surface(
                                                    color = Color(0xFFF3F4F6),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "FÚTBOL 7",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color(0xFF374151),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // 1. Horario Diurno
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(14.dp),
                                                color = Color(0xFFFFFBEB),
                                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text("☀️", fontSize = 18.sp)
                                                            Spacer(Modifier.width(8.dp))
                                                            Column {
                                                                Text("Horario Diurno", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                                                Text("08:00 a 18:00 hrs (Luz Solar)", fontSize = 10.sp, color = Color(0xFFB45309))
                                                            }
                                                        }

                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text("S/ ${priceDayF7.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E))
                                                            Text("por hora", fontSize = 9.sp, color = Color(0xFFB45309))
                                                        }
                                                    }

                                                    Spacer(Modifier.height(8.dp))

                                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        listOf(60.0, 70.0, 80.0).forEach { fee ->
                                                            val isSel = priceDayF7 == fee
                                                            Surface(
                                                                onClick = { priceDayF7 = fee },
                                                                shape = RoundedCornerShape(8.dp),
                                                                color = if (isSel) DarkForestGreen else Color.White,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Text(
                                                                    text = "S/ ${fee.toInt()}",
                                                                    fontSize = 11.5.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isSel) LimeNeon else Color(0xFF78350F),
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.padding(vertical = 6.dp)
                                                                )
                                                            }
                                                        }
                                                        Surface(
                                                            shape = RoundedCornerShape(8.dp),
                                                            color = Color.White,
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Text(
                                                                text = "Otro",
                                                                fontSize = 11.5.sp,
                                                                color = Color(0xFF78350F),
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier.padding(vertical = 6.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.height(10.dp))

                                            // 2. Horario Nocturno
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(14.dp),
                                                color = Color(0xFFF0FDF4),
                                                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text("🌙", fontSize = 18.sp)
                                                            Spacer(Modifier.width(8.dp))
                                                            Column {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Text("Horario Nocturno", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                                                                    Spacer(Modifier.width(4.dp))
                                                                    Surface(color = LimeNeon, shape = RoundedCornerShape(4.dp)) {
                                                                        Text("LED ON", fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = DarkForestGreen, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                                    }
                                                                }
                                                                Text("18:00 a 23:00 hrs (Horario Prime)", fontSize = 10.sp, color = ForestGreen)
                                                            }
                                                        }

                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text("S/ ${priceNightF7.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                                            Text("por hora", fontSize = 9.sp, color = ForestGreen)
                                                        }
                                                    }

                                                    Spacer(Modifier.height(8.dp))

                                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        listOf(90.0, 100.0, 110.0, 120.0).forEach { fee ->
                                                            val isSel = priceNightF7 == fee
                                                            Surface(
                                                                onClick = { priceNightF7 = fee },
                                                                shape = RoundedCornerShape(8.dp),
                                                                color = if (isSel) DarkForestGreen else Color.White,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Text(
                                                                    text = "S/ ${fee.toInt()}",
                                                                    fontSize = 11.5.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isSel) LimeNeon else DarkForestGreen,
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.padding(vertical = 6.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            // 3. Tarjeta Tarifa Campo F11 Completo
                                            Spacer(Modifier.height(10.dp))
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(14.dp),
                                                color = DarkForestGreen
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(14.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Outlined.Stadium, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(24.dp))
                                                        Spacer(Modifier.width(10.dp))
                                                        Column {
                                                            Text("TARIFA CAMPO F11 COMPLETO", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                            Text("Gran formato para campeonatos oficiales", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.75f))
                                                        }
                                                    }

                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text("Día: ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                                            Text("S/ ${priceDayF11.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                                        }
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text("Noche LED: ", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                                            Text("S/ ${priceNightF11.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // ==========================================
                                    // CARD 5: CANCHA TECHADA O VIP
                                    // ==========================================
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(color = LimeNeon, shape = RoundedCornerShape(6.dp)) {
                                                        Text("VIP", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DarkForestGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                    Spacer(Modifier.width(8.dp))
                                                    Column {
                                                        Text("Cancha Techada o VIP", fontSize = 15.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                        Text("Protección contra sol y lluvia con tarifa diferencial", fontSize = 10.5.sp, color = TextSecondary)
                                                    }
                                                }

                                                Switch(
                                                    checked = hasVipCourt,
                                                    onCheckedChange = { hasVipCourt = it },
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = ForestGreen
                                                    )
                                                )
                                            }

                                            if (hasVipCourt) {
                                                Spacer(Modifier.height(12.dp))
                                                Surface(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFFF9FAFB),
                                                    border = BorderStroke(1.dp, BorderGray)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                                                Text("▲ TECHADA • LLUVIA CERO", fontSize = 8.sp, fontWeight = FontWeight.Black, color = LimeNeon, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                            }
                                                            Text("Alta Demanda Invierno", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                                        }

                                                        Spacer(Modifier.height(10.dp))
                                                        FormLabel("Identificador de Cancha VIP")
                                                        OutlinedTextField(
                                                            value = vipCourtName,
                                                            onValueChange = { vipCourtName = it },
                                                            placeholder = { Text("Cancha 1 Techada Pro LED") },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(10.dp),
                                                            singleLine = true,
                                                            colors = modernInputColors()
                                                        )

                                                        Spacer(Modifier.height(10.dp))
                                                        FormLabel("Tarifa VIP por Hora (Cualquier horario)")
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text("S/ ${vipCourtPrice.toInt()}.00", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                            Text("/ hr fija", fontSize = 11.sp, color = TextSecondary)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // ==========================================
                                    // CARD 6: FACTURACIÓN Y COBROS
                                    // ==========================================
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column(modifier = Modifier.padding(18.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0xFFF3F4F6)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("🧾", fontSize = 18.sp)
                                                }
                                                Spacer(Modifier.width(10.dp))
                                                Column {
                                                    Text("Facturación y Cobros", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                    Text("Para desembolsos semanales de señas de reserva", fontSize = 11.sp, color = TextSecondary)
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            FormLabel("RUC (11 dígitos) o DNI del Titular")
                                            OutlinedTextField(
                                                value = complexRuc,
                                                onValueChange = { if (it.length <= 11 && it.all { c -> c.isDigit() }) complexRuc = it },
                                                placeholder = { Text("20608492011 - Inversiones Deportivas El Oasis SAC") },
                                                leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp)) },
                                                modifier = Modifier.fillMaxWidth().testTag("court_ruc_input"),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                colors = modernInputColors()
                                            )

                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                text = "🔒 Datos encriptados. Se emplean exclusivamente para boletas de campeonatos.",
                                                fontSize = 10.5.sp,
                                                color = TextSecondary
                                            )

                                            Spacer(Modifier.height(14.dp))

                                            Row(verticalAlignment = Alignment.Top) {
                                                Checkbox(
                                                    checked = acceptedTerms,
                                                    onCheckedChange = { acceptedTerms = it },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = ForestGreen,
                                                        checkmarkColor = Color.White,
                                                        uncheckedColor = Color(0xFF9CA3AF)
                                                    ),
                                                    modifier = Modifier.size(22.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = "Declaro que las instalaciones cumplen con las normas municipales de Chiclayo y acepto las Políticas de Alquiler y Arbitraje de ChocoFulbo.",
                                                    fontSize = 11.5.sp,
                                                    color = TextPrimary,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // PESTAÑA 2: ACREDITACIÓN ÁRBITRO (Licencia CODAR, Categoría, Tarifa)
                            if (step3ActiveTab == 2) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("⚖️", fontSize = 22.sp)
                                            Spacer(Modifier.width(10.dp))
                                            Column {
                                                Text("Colegiado CODAR Lambayeque", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                                Text("Registro para arbitrar partidos oficiales y pichangas", fontSize = 11.5.sp, color = TextSecondary)
                                            }
                                        }

                                        Spacer(Modifier.height(14.dp))

                                        FormLabel("Nº de Carnet / Licencia CODAR *")
                                        OutlinedTextField(
                                            value = refereeLicense,
                                            onValueChange = { refereeLicense = it },
                                            placeholder = { Text("Ej: CODAR-LAMB-2025-0489") },
                                            leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = Color(0xFF6B7280)) },
                                            modifier = Modifier.fillMaxWidth().testTag("referee_license_input"),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            colors = modernInputColors()
                                        )

                                        Spacer(Modifier.height(12.dp))

                                        FormLabel("Categoría Arbitral")
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
                                                    color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                                    border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = cat.replace(" Categoría", "").replace(" FPF", ""),
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) LimeNeon else TextPrimary,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(vertical = 7.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))

                                        FormLabel("Tarifa por Partido")
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
                                                    color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                                    border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = "S/ ${fee.toInt()}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) LimeNeon else TextPrimary,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Checkbox: Términos y Código de Juego Limpio
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
                                    modifier = Modifier.size(22.dp).testTag("accept_terms_checkbox")
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Acepto los Términos de ChocoFulbo y me comprometo al Código de Juego Limpio de Chiclayo (cero violencia en los campos deportivos).",
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // Botones de Navegación: Atrás y CREAR MI CUENTA (Vibrante Lime Neon!)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        validationError = null
                                        currentStep = 2
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFE5E7EB),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = DarkForestGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("Atrás", fontWeight = FontWeight.Bold, color = DarkForestGreen, fontSize = 14.sp)
                                    }
                                }

                                Button(
                                    onClick = onRegisterSubmit,
                                    modifier = Modifier
                                        .weight(2.2f)
                                        .height(52.dp)
                                        .testTag("submit_register_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LimeNeon,
                                        contentColor = DarkForestGreen
                                    ),
                                    enabled = authState !is AuthState.Loading
                                ) {
                                    if (authState is AuthState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = DarkForestGreen,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Crear Mi Cuenta",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 15.sp,
                                                color = DarkForestGreen
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text("🚀", fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
            Spacer(Modifier.navigationBarsPadding())
        }
    }

    // Modal de Ayuda
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    "¿Cómo registrarte en ChocoFulbo?",
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "1. Crea tu cuenta con tu número de Chiclayo.\n2. Elige si eres Jugador, Dueño de Cancha o Árbitro colegiado.\n3. Completa tu ficha técnica (posición preferida o datos de tu sede).\n\n¡Listo! Podrás jugar y armar pichangas de inmediato.",
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
                    Text("¡Entendido!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

/**
 * Tarjeta Flotante del Stepper / Línea de Tiempo
 */
@Composable
private fun RegistrationTimelineCard(currentStep: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Fila superior: Chip de Paso y Porcentaje completado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = LimeNeon,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = when (currentStep) {
                            1 -> "PASO 1 DE 3 • IDENTIDAD"
                            2 -> "PASO 2 DE 3 • SELECCIÓN DE ROL"
                            else -> "PASO 3 DE 3 • FICHA TÉCNICA"
                        },
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = when (currentStep) {
                        1 -> "33% completado"
                        2 -> "66% completado"
                        else -> "100% completado"
                    },
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen
                )
            }

            Spacer(Modifier.height(14.dp))

            // Fila de Nodos Conectores (Iconos y Líneas de Progreso)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nodo 1: Cuenta
                TimelineStepNode(
                    icon = "✓",
                    label = "Cuenta",
                    isCompleted = currentStep > 1,
                    isActive = currentStep == 1,
                    isCheck = currentStep >= 1
                )

                // Línea 1 -> 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (currentStep >= 2) LimeNeon else Color(0xFFE5E7EB))
                )

                // Nodo 2: Tu Rol
                TimelineStepNode(
                    icon = if (currentStep > 2) "✓" else "⚽",
                    label = "Tu Rol",
                    isCompleted = currentStep > 2,
                    isActive = currentStep == 2,
                    isCheck = currentStep > 2
                )

                // Línea 2 -> 3
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (currentStep >= 3) LimeNeon else Color(0xFFE5E7EB))
                )

                // Nodo 3: Ficha
                TimelineStepNode(
                    icon = if (currentStep == 3) "✓" else "🪪",
                    label = "Ficha",
                    isCompleted = currentStep == 3,
                    isActive = currentStep == 3,
                    isCheck = currentStep == 3
                )
            }
        }
    }
}

@Composable
private fun TimelineStepNode(
    icon: String,
    label: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isCheck: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted || (isActive && isCheck) -> DarkForestGreen
                        isActive -> DarkForestGreen
                        else -> Color(0xFFF3F4F6)
                    }
                )
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = when {
                        isActive -> LimeNeon
                        isCompleted -> DarkForestGreen
                        else -> Color(0xFFE5E7EB)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = if (isActive || isCompleted) LimeNeon else Color(0xFF9CA3AF),
                fontSize = if (icon == "✓") 16.sp else 13.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isActive) DarkForestGreen else TextSecondary
        )
    }
}

/**
 * Tarjeta de Selección de Rol (Paso 2)
 */
@Composable
private fun RoleOptionCard(
    icon: String,
    title: String,
    badgeText: String,
    badgeColor: Color,
    badgeTextColor: Color,
    subtitle: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) LimeNeon else Color(0xFFE5E7EB)),
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Caja con icono del rol
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) DarkForestGreen else Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 20.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = badgeTextColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Check circle
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = LimeNeon, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 11.5.sp,
                color = TextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(start = 56.dp)
            )
        }
    }
}

/**
 * Tarjeta Detallada de Posición Táctica (Paso 3)
 */
@Composable
private fun DetailedPositionCard(
    position: String,
    tag: String,
    tagColor: Color,
    desc: String,
    icon: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF9FAFB),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) LimeNeon else BorderGray),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 18.sp)
                Surface(
                    color = Color.Transparent
                ) {
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) DarkForestGreen else tagColor
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = position,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) DarkForestGreen else TextPrimary
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun FormLabel(text: String, modifier: Modifier = Modifier.padding(bottom = 5.dp)) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = DarkForestGreen,
        letterSpacing = 0.4.sp,
        fontSize = 12.sp,
        modifier = modifier
    )
}

@Composable
private fun modernInputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = DarkForestGreen,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = Color(0xFFF9FAFB),
    unfocusedContainerColor = Color(0xFFF9FAFB),
    cursorColor = DarkForestGreen
)

private fun calculatePasswordStrength(pass: String): Pair<Int, String> {
    if (pass.isEmpty()) return 0 to ""
    if (pass.length < 6) return 1 to "Débil"
    var score = 1
    if (pass.length >= 8) score++
    if (pass.any { it.isDigit() } && pass.any { it.isLetter() }) score++
    if (pass.any { !it.isLetterOrDigit() } || (pass.length >= 10 && pass.any { it.isUpperCase() })) score++
    val label = when (score) {
        1 -> "Débil"
        2 -> "Media"
        3 -> "Buena"
        else -> "Fuerte"
    }
    return score to label
}
