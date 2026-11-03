package com.incanatoapps.retofut.ui.screens.courts

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.UserRole
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtManagementScreen(
    navController: NavHostController,
    viewModel: CourtManagementViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userRole = remember { sessionManager.fetchUserRole() }

    BackHandler { navController.popBackStack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Horarios", "Tarifas", "Mi Sede", "Torneos")

    // Estados locales para diálogos
    var editDayRateText by remember { mutableStateOf(viewModel.complex.dayRate.toInt().toString()) }
    var editNightRateText by remember { mutableStateOf(viewModel.complex.nightRate.toInt().toString()) }
    var editWeekendRateText by remember { mutableStateOf(viewModel.complex.weekendRate.toInt().toString()) }

    var editComplexName by remember { mutableStateOf(viewModel.complex.name) }
    var editComplexAddress by remember { mutableStateOf(viewModel.complex.address) }
    var editComplexDistrict by remember { mutableStateOf(viewModel.complex.district) }
    var editComplexPhone by remember { mutableStateOf(viewModel.complex.phone) }

    var newCourtName by remember { mutableStateOf("Cancha 5") }
    var newCourtFormat by remember { mutableStateOf("Fútbol 7") }
    var newCourtGrass by remember { mutableStateOf("Césped Sintético Monofilamento") }

    var manualClientName by remember { mutableStateOf("") }
    var manualClientPhone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("court_mgmt_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Atrás",
                            tint = TextPrimary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Panel Admin de Cancha",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "ADMIN CANCHA",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF991B1B),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = viewModel.complex.name,
                            fontSize = 11.5.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Selector interactivo de rol para pruebas rápidas
                    Surface(
                        onClick = {
                            viewModel.isAdminRoleActive = !viewModel.isAdminRoleActive
                            Toast.makeText(
                                context,
                                if (viewModel.isAdminRoleActive) "Modo ADMIN DE CANCHA activo (Acceso total a gestión)"
                                else "Modo VISUALIZACIÓN CLIENTE activo",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (viewModel.isAdminRoleActive) Color(0xFFFEF3C7) else Color(0xFFF3F4F6),
                        border = BorderStroke(1.dp, if (viewModel.isAdminRoleActive) Color(0xFFF59E0B) else BorderGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (viewModel.isAdminRoleActive) Icons.Outlined.AdminPanelSettings else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = if (viewModel.isAdminRoleActive) Color(0xFF92400E) else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (viewModel.isAdminRoleActive) "Admin On" else "Cliente",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (viewModel.isAdminRoleActive) Color(0xFF92400E) else TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                }

                // TabRow M3
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = DarkForestGreen,
                    divider = { HorizontalDivider(color = BorderGray, thickness = 1.dp) }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) DarkForestGreen else TextSecondary
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = { ChocoFulboBottomBar(navController = navController) },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            when (selectedTabIndex) {
                // ==========================================
                // TAB 0: GESTIÓN DE HORARIOS Y TURNOS
                // ==========================================
                0 -> {
                    // Selector de Cancha
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "SELECCIONA LA CANCHA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "${viewModel.courts.size} Canchas registradas",
                                    fontSize = 11.sp,
                                    color = ForestGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(viewModel.courts) { court ->
                                    val isSelected = viewModel.selectedCourtId == court.id
                                    Surface(
                                        onClick = { viewModel.onSelectCourt(court.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) DarkForestGreen else Color.White,
                                        border = BorderStroke(1.2.dp, if (isSelected) DarkForestGreen else BorderGray),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) LimeNeon else Color(0xFFF3F4F6)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "${court.id}",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp,
                                                    color = if (isSelected) DarkForestGreen else TextPrimary
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    court.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = if (isSelected) Color.White else TextPrimary
                                                )
                                                Text(
                                                    court.format,
                                                    fontSize = 9.5.sp,
                                                    color = if (isSelected) LimeNeon else TextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Selector de Fecha
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            Text(
                                "FECHA DE OPERACIÓN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Hoy (Viernes)", "Mañana (Sábado)", "Domingo 25", "Lunes 26").forEach { d ->
                                    val isSelected = viewModel.selectedDate == d
                                    Surface(
                                        onClick = { viewModel.onSelectDate(d) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFDCFCE7) else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) ForestGreen else BorderGray),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 10.dp)
                                        ) {
                                            Text(
                                                d,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                                color = if (isSelected) DarkForestGreen else TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Resumen de Ocupación del Día
                    item {
                        val availableCount = viewModel.scheduleSlots.count { it.status == TimeSlotStatus.DISPONIBLE }
                        val reservedCount = viewModel.scheduleSlots.count { it.status == TimeSlotStatus.RESERVADO }
                        val maintCount = viewModel.scheduleSlots.count { it.status == TimeSlotStatus.MANTENIMIENTO }
                        val projectedIncome = viewModel.scheduleSlots.filter { it.status == TimeSlotStatus.RESERVADO }.sumOf { it.price }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "ESTADO DIARIO DE HORARIOS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        color = DarkForestGreen
                                    )
                                    Text(
                                        "Ingreso Proyectado: S/. ${projectedIncome.toInt()}.00",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        color = ForestGreen
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Disponibles
                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$availableCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                            Text("DISPONIBLES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                        }
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    // Reservados
                                    Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$reservedCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF991B1B))
                                            Text("OCUPADOS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                        }
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    // Mantenimiento
                                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$maintCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E))
                                            Text("MANTENIMIENTO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Grilla / Lista de Turnos Horarios
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "TURNOS DEL DÍA (TOCA PARA GESTIONAR)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "08:00 a 23:00 hrs",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    items(viewModel.scheduleSlots, key = { it.id }) { slot ->
                        val bg = when (slot.status) {
                            TimeSlotStatus.DISPONIBLE -> Color.White
                            TimeSlotStatus.RESERVADO -> Color(0xFFFFF1F2)
                            TimeSlotStatus.MANTENIMIENTO -> Color(0xFFFFFBEB)
                        }
                        val borderColor = when (slot.status) {
                            TimeSlotStatus.DISPONIBLE -> BorderGray
                            TimeSlotStatus.RESERVADO -> Color(0xFFFECDD3)
                            TimeSlotStatus.MANTENIMIENTO -> Color(0xFFFDE68A)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = bg),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (slot.isNight) DarkForestGreen else Color(0xFFE5E7EB)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            if (slot.isNight) Icons.Outlined.Schedule else Icons.Outlined.CalendarToday,
                                            contentDescription = null,
                                            tint = if (slot.isNight) LimeNeon else TextPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                slot.time,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = TextPrimary
                                            )
                                            if (slot.isNight) {
                                                Spacer(Modifier.width(4.dp))
                                                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                                    Text("LUZ LED", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 3.dp))
                                                }
                                            }
                                        }
                                        Text(
                                            text = when (slot.status) {
                                                TimeSlotStatus.DISPONIBLE -> "Turno libre • S/. ${slot.price.toInt()}.00/hr"
                                                TimeSlotStatus.RESERVADO -> slot.reservedBy ?: "Reservado"
                                                TimeSlotStatus.MANTENIMIENTO -> "Regado / Mantenimiento de césped"
                                            },
                                            fontSize = 11.sp,
                                            color = when (slot.status) {
                                                TimeSlotStatus.DISPONIBLE -> ForestGreen
                                                TimeSlotStatus.RESERVADO -> Color(0xFFDC2626)
                                                TimeSlotStatus.MANTENIMIENTO -> Color(0xFFB45309)
                                            },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Botones de Acción para el Admin
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        onClick = { viewModel.toggleSlotStatus(slot) },
                                        color = when (slot.status) {
                                            TimeSlotStatus.DISPONIBLE -> Color(0xFFDCFCE7)
                                            TimeSlotStatus.RESERVADO -> Color(0xFFFEE2E2)
                                            TimeSlotStatus.MANTENIMIENTO -> Color(0xFFFEF3C7)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = slot.status.displayName.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = when (slot.status) {
                                                TimeSlotStatus.DISPONIBLE -> DarkForestGreen
                                                TimeSlotStatus.RESERVADO -> Color(0xFF991B1B)
                                                TimeSlotStatus.MANTENIMIENTO -> Color(0xFF92400E)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    if (viewModel.isAdminRoleActive) {
                                        Spacer(Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                viewModel.selectedSlotForAction = slot
                                                manualClientName = slot.reservedBy?.replace(" (Presencial)", "")?.replace(" (Reserva App)", "") ?: ""
                                                manualClientPhone = slot.contactPhone ?: ""
                                                viewModel.showManualBookingDialog = true
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Edit,
                                                contentDescription = "Editar turno",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // TAB 1: GESTIÓN DE TARIFAS POR HORA
                // ==========================================
                1 -> {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "TARIFAS OFICIALES",
                                                color = LimeNeon,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                "Precios por hora de alquiler",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Button(
                                            onClick = { viewModel.showEditRatesDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("edit_rates_button")
                                        ) {
                                            Icon(Icons.Outlined.Tune, null, tint = Color.White, modifier = Modifier.size(15.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Modificar", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                                        }
                                    }

                                    Spacer(Modifier.height(14.dp))

                                    // Tres tarjetas de tarifa
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            color = Color.White.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("DIURNO", color = LimeNeon, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                                Text("S/. ${viewModel.complex.dayRate.toInt()}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                Text("08:00 a 18:00", color = Color.White.copy(alpha = 0.7f), fontSize = 9.5.sp)
                                            }
                                        }

                                        Surface(
                                            color = Color.White.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("NOCTURNO", color = Color(0xFFFDE68A), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                                Text("S/. ${viewModel.complex.nightRate.toInt()}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                Text("Con luces LED", color = Color.White.copy(alpha = 0.7f), fontSize = 9.5.sp)
                                            }
                                        }

                                        Surface(
                                            color = Color.White.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("FIN DE SEMANA", color = Color(0xFF93C5FD), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                                Text("S/. ${viewModel.complex.weekendRate.toInt()}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                Text("Sáb & Dom", color = Color.White.copy(alpha = 0.7f), fontSize = 9.5.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                "ESTIMADO DE CUOTA POR JUGADOR SEGÚN FORMATO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(Modifier.height(8.dp))

                            listOf(
                                Triple("Fútbol 5 (10 jugadores)", (viewModel.complex.nightRate / 10), "S/. ${viewModel.complex.nightRate.toInt()} / 10 = S/. ${String.format("%.1f", viewModel.complex.nightRate / 10)} c/u"),
                                Triple("Fútbol 7 (14 jugadores)", (viewModel.complex.nightRate / 14), "S/. ${viewModel.complex.nightRate.toInt()} / 14 = S/. ${String.format("%.1f", viewModel.complex.nightRate / 14)} c/u"),
                                Triple("Fútbol 8 (16 jugadores)", (viewModel.complex.nightRate / 16), "S/. ${viewModel.complex.nightRate.toInt()} / 16 = S/. ${String.format("%.1f", viewModel.complex.nightRate / 16)} c/u"),
                                Triple("Fútbol 11 (22 jugadores)", (viewModel.complex.nightRate / 22), "S/. ${viewModel.complex.nightRate.toInt()} / 22 = S/. ${String.format("%.1f", viewModel.complex.nightRate / 22)} c/u")
                            ).forEach { (format, cuota, calc) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, BorderGray)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(format, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text(calc, fontSize = 11.sp, color = TextSecondary)
                                        }
                                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)) {
                                            Text(
                                                "S/. ${String.format("%.1f", cuota)}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = DarkForestGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // TAB 2: INFORMACIÓN DE LA SEDE Y CANCHAS
                // ==========================================
                2 -> {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Ficha de Complejo Deportivo
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderGray)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    viewModel.complex.name,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 16.sp,
                                                    color = TextPrimary
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Icon(Icons.Outlined.Verified, contentDescription = "Verificado", tint = ForestGreen, modifier = Modifier.size(16.dp))
                                            }
                                            Text(
                                                "${viewModel.complex.district} • ⭐ ${viewModel.complex.rating} (${viewModel.complex.totalReviews} reseñas)",
                                                fontSize = 11.5.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        IconButton(onClick = { viewModel.showEditComplexDialog = true }) {
                                            Icon(Icons.Outlined.Edit, "Editar Sede", tint = ForestGreen)
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))
                                    HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                                    Spacer(Modifier.height(10.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.LocationOn, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(viewModel.complex.address, fontSize = 12.5.sp, color = TextPrimary)
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Phone, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(viewModel.complex.phone, fontSize = 12.5.sp, color = TextPrimary)
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        "SERVICIOS E INSTALACIONES INCLUIDAS:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextSecondary
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    viewModel.complex.amenities.forEach { am ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Outlined.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(am, fontSize = 11.5.sp, color = TextPrimary)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Lista de Canchas en esta Sede
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "CANCHAS DISPONIBLES EN ESTA SEDE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Button(
                                    onClick = { viewModel.showAddCourtDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Outlined.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("Agregar Cancha", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            viewModel.courts.forEach { court ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, BorderGray)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFDCFCE7)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.SportsSoccer, null, tint = DarkForestGreen, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(court.name, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimary)
                                                Spacer(Modifier.width(6.dp))
                                                Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                                    Text(court.format, color = LimeNeon, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                                                }
                                            }
                                            Text(court.grassType, fontSize = 11.sp, color = TextSecondary)
                                        }
                                        Surface(color = Color(0xFFF0FDF4), shape = RoundedCornerShape(6.dp)) {
                                            Text("OPERATIVA", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ForestGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // TAB 3: TORNEOS PROPIOS DE LA SEDE
                // ==========================================
                3 -> {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.2.dp, LimeNeon)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(DarkForestGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.EmojiEvents, null, tint = LimeNeon, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Organiza Torneos en tu Sede",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Crea copas relámpago, abre inscripciones a equipos y llena tus canchas en horarios estelares.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "TORNEOS EN EL OASIS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Button(
                                    onClick = { navController.navigate(Screen.Tournaments.route) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("go_to_tournaments_module_btn")
                                ) {
                                    Icon(Icons.Outlined.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Crear Nuevo Torneo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            viewModel.venueTournaments.forEach { t ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable { navController.navigate(Screen.Tournaments.route) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, BorderGray)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(t.name, fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
                                            Surface(
                                                color = if (t.status == "INSCRIPCIONES_ABIERTAS") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    if (t.status == "INSCRIPCIONES_ABIERTAS") "INSCRIPCIONES" else "EN CURSO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (t.status == "INSCRIPCIONES_ABIERTAS") ForestGreen else Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))
                                        Text("${t.format} • ${t.startDate}", fontSize = 11.5.sp, color = TextSecondary)
                                        Spacer(Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Outlined.Groups, null, tint = ForestGreen, modifier = Modifier.size(15.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("${t.teamsRegistered}/${t.maxTeams} Equipos", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                                            }
                                            Text("Premio: ${t.prize}", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIÁLOGOS DE GESTIÓN ADMIN
    // ==========================================

    // 1. Diálogo de Reserva Manual de Turno
    if (viewModel.showManualBookingDialog && viewModel.selectedSlotForAction != null) {
        val slot = viewModel.selectedSlotForAction!!
        Dialog(onDismissRequest = { viewModel.showManualBookingDialog = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Gestión de Turno: ${slot.time}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkForestGreen)
                    Text("Cancha seleccionada • ${slot.date}", fontSize = 11.5.sp, color = TextSecondary)

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = manualClientName,
                        onValueChange = { manualClientName = it },
                        label = { Text("Nombre del Cliente / Equipo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = manualClientPhone,
                        onValueChange = { manualClientPhone = it },
                        label = { Text("Teléfono / WhatsApp de Contacto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.releaseSlot(slot.id)
                                viewModel.showManualBookingDialog = false
                                Toast.makeText(context, "Turno liberado como DISPONIBLE", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Liberar", color = Color(0xFFDC2626), fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (manualClientName.isBlank()) {
                                    Toast.makeText(context, "Ingresa el nombre del equipo o cliente", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.bookSlotManually(slot.id, manualClientName, manualClientPhone)
                                    Toast.makeText(context, "Reserva agendada con éxito", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // 2. Diálogo de Edición de Tarifas
    if (viewModel.showEditRatesDialog) {
        Dialog(onDismissRequest = { viewModel.showEditRatesDialog = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Ajustar Tarifas por Hora", fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkForestGreen)
                    Text("Define los precios por hora para tu complejo", fontSize = 11.5.sp, color = TextSecondary)

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editDayRateText,
                        onValueChange = { editDayRateText = it },
                        label = { Text("Tarifa Diurna (S/.)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editNightRateText,
                        onValueChange = { editNightRateText = it },
                        label = { Text("Tarifa Nocturna con Luces (S/.)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editWeekendRateText,
                        onValueChange = { editWeekendRateText = it },
                        label = { Text("Tarifa Fin de Semana (S/.)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.showEditRatesDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val day = editDayRateText.toDoubleOrNull() ?: viewModel.complex.dayRate
                                val night = editNightRateText.toDoubleOrNull() ?: viewModel.complex.nightRate
                                val weekend = editWeekendRateText.toDoubleOrNull() ?: viewModel.complex.weekendRate
                                viewModel.updateRates(day, night, weekend)
                                Toast.makeText(context, "Tarifas actualizadas correctamente", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Actualizar", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // 3. Diálogo de Edición de Sede
    if (viewModel.showEditComplexDialog) {
        Dialog(onDismissRequest = { viewModel.showEditComplexDialog = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Editar Datos del Local", fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkForestGreen)

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editComplexName,
                        onValueChange = { editComplexName = it },
                        label = { Text("Nombre del Complejo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editComplexAddress,
                        onValueChange = { editComplexAddress = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editComplexDistrict,
                        onValueChange = { editComplexDistrict = it },
                        label = { Text("Distrito") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editComplexPhone,
                        onValueChange = { editComplexPhone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.showEditComplexDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cerrar")
                        }
                        Button(
                            onClick = {
                                viewModel.updateComplexInfo(editComplexName, editComplexAddress, editComplexDistrict, editComplexPhone)
                                Toast.makeText(context, "Datos de sede guardados", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 4. Diálogo de Añadir Cancha
    if (viewModel.showAddCourtDialog) {
        Dialog(onDismissRequest = { viewModel.showAddCourtDialog = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Agregar Nueva Cancha", fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkForestGreen)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newCourtName,
                        onValueChange = { newCourtName = it },
                        label = { Text("Nombre de la Cancha (ej. Cancha 5)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Formato de Cancha:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                        listOf("Fútbol 5", "Fútbol 7", "Fútbol 8", "Fútbol 11").forEach { fmt ->
                            val isSel = newCourtFormat == fmt
                            Surface(
                                onClick = { newCourtFormat = fmt },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
                            ) {
                                Text(fmt, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isSel) ForestGreen else TextPrimary, modifier = Modifier.padding(6.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCourtGrass,
                        onValueChange = { newCourtGrass = it },
                        label = { Text("Tipo de Césped") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.showAddCourtDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                viewModel.addCourt(newCourtName, newCourtFormat, newCourtGrass)
                                Toast.makeText(context, "Cancha agregada al complejo", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Registrar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
