package com.incanatoapps.retofut.ui.screens.courts

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun CourtShiftsTab(
    viewModel: CourtManagementViewModel,
    onCreateTournamentClick: () -> Unit,
    onReviewDepositsClick: () -> Unit,
    onAssignShiftClick: (CourtShiftUiModel) -> Unit
) {
    val context = LocalContext.current
    val visibleShifts = viewModel.getVisibleShifts()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HERO COMPLEX CARD (Dark Forest Green)
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("court_hero_card"),
                shape = RoundedCornerShape(22.dp),
                color = DarkForestGreen,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ADMINISTRACIÓN DE SEDE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF86EFAC),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = viewModel.complexName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "(${viewModel.complexDistrict})",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(10.dp))

                    // Badge canchas operativas
                    Surface(
                        color = Color(0xFF0F3D28),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF166534))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(LimeNeon)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${viewModel.courtsCount} Canchas Operativas (${viewModel.turfType})",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Botón Crear Torneo Relámpago
                    Button(
                        onClick = onCreateTournamentClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("create_relampago_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LimeNeon,
                            contentColor = DarkForestGreen
                        )
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = DarkForestGreen)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Crear Torneo Relámpago en mi Sede",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. RECAUDADO HOY & MÉTRICAS
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("court_metrics_card"),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RECAUDADO HOY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "S/ ${"%.2f".format(viewModel.currentDayRevenue)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "Yape / Efectivo",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Ocupación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ocupación: ${viewModel.currentDayOccupiedSlots}/8 Turnos Ocupados",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${viewModel.currentDayOccupancyPercent}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { viewModel.currentDayOccupancyPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ForestGreen,
                        trackColor = Color(0xFFE5E7EB)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Banner de Señas Pendientes
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReviewDepositsClick() },
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFEE2E2))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${viewModel.pendingDepositsCount} señas por validar",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Text(
                                text = "Revisar >",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. COURT SELECTOR (Cancha 1 / Cancha 2)
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cancha 1 • F7
                val isCancha1 = viewModel.selectedCourtTab == 0
                Surface(
                    onClick = { viewModel.selectedCourtTab = 0 },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCancha1) DarkForestGreen else Color(0xFFE5E7EB)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.SportsSoccer,
                            contentDescription = null,
                            tint = if (isCancha1) LimeNeon else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Cancha 1 • F7",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCancha1) Color.White else TextPrimary
                        )
                    }
                }

                // Cancha 2 • F6
                val isCancha2 = viewModel.selectedCourtTab == 1
                Surface(
                    onClick = { viewModel.selectedCourtTab = 1 },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCancha2) DarkForestGreen else Color(0xFFE5E7EB)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.Stadium,
                            contentDescription = null,
                            tint = if (isCancha2) LimeNeon else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Cancha 2 • F6",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCancha2) Color.White else TextPrimary
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. DAY SELECTOR CHIPS
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Hoy (Viernes)", "Mañana (Sábado)", "Domingo 16").forEach { dayLabel ->
                    val isSelected = viewModel.selectedDay == dayLabel
                    Surface(
                        onClick = { viewModel.selectedDay = dayLabel },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) DarkForestGreen else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(LimeNeon)
                                )
                                Spacer(Modifier.width(5.dp))
                            }
                            Text(
                                text = dayLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 5. SHIFT TIMELINE CARDS
        // ==========================================
        items(visibleShifts, key = { it.id }) { shift ->
            ShiftCard(
                shift = shift,
                onConfirmClick = {
                    viewModel.confirmPendingShift(shift.id)
                    Toast.makeText(context, "Turno confirmado exitosamente", Toast.LENGTH_SHORT).show()
                },
                onAssignClick = { onAssignShiftClick(shift) }
            )
        }

        // ==========================================
        // 6. BOTTOM: VALIDACIÓN RÁPIDA DE PAGOS
        // ==========================================
        item {
            if (viewModel.pendingPaymentSlips.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ForestGreen)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Validación Rápida de Pagos",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen
                        )
                    }

                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${viewModel.pendingPaymentSlips.size} de 2 pendientes",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                viewModel.pendingPaymentSlips.forEach { slip ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Voucher Thumbnail
                                Surface(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    color = Color(0xFF7C3AED)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("📱 YAPE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }

                                Spacer(Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(slip.senderName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Operación ${slip.operationNumber} • ${slip.timeStr}", fontSize = 10.5.sp, color = TextSecondary)
                                    Text(slip.shiftInfo, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = ForestGreen)
                                }

                                Text(
                                    text = "S/ ${"%.2f".format(slip.amount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.validateVoucher(slip.id)
                                        Toast.makeText(context, "Pago de S/ ${slip.amount} validado", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1.8f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkForestGreen,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("✔ Validar ✔", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.rejectVoucher(slip.id)
                                        Toast.makeText(context, "Comprobante rechazado", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, BorderGray)
                                ) {
                                    Text("Rechazar", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShiftCard(
    shift: CourtShiftUiModel,
    onConfirmClick: () -> Unit,
    onAssignClick: () -> Unit
) {
    val borderColor = when (shift.status) {
        ShiftStatus.POR_VALIDAR -> Color(0xFFF59E0B)
        ShiftStatus.TURNO_LIBRE -> Color(0xFF22C55E)
        else -> Color(0xFFE5E7EB)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(if (shift.status == ShiftStatus.POR_VALIDAR || shift.status == ShiftStatus.TURNO_LIBRE) 1.5.dp else 1.dp, borderColor),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header del Turno
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = shift.timeRange,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(Modifier.width(8.dp))

                    // Status Badge
                    Surface(
                        color = when (shift.status) {
                            ShiftStatus.RESERVADO -> DarkForestGreen
                            ShiftStatus.POR_VALIDAR -> Color(0xFFFEF3C7)
                            ShiftStatus.TURNO_LIBRE -> Color(0xFFDCFCE7)
                            ShiftStatus.EN_JUEGO -> DarkForestGreen
                            ShiftStatus.BLOQUEADO -> Color(0xFFF3F4F6)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (shift.status == ShiftStatus.RESERVADO || shift.status == ShiftStatus.EN_JUEGO) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(LimeNeon)
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(
                                text = shift.status.label,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = when (shift.status) {
                                    ShiftStatus.RESERVADO -> LimeNeon
                                    ShiftStatus.POR_VALIDAR -> Color(0xFFB45309)
                                    ShiftStatus.TURNO_LIBRE -> ForestGreen
                                    ShiftStatus.EN_JUEGO -> LimeNeon
                                    ShiftStatus.BLOQUEADO -> TextSecondary
                                }
                            )
                        }
                    }
                }

                // Precio / Ronda
                if (shift.roundInfo != null) {
                    Text(
                        text = shift.roundInfo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreen
                    )
                } else {
                    Text(
                        text = if (shift.hasLedLighting) "✦ S/ ${"%.2f".format(shift.price)} (Luz LED)" else "S/ ${"%.2f".format(shift.price)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (shift.hasLedLighting) ForestGreen else DarkForestGreen
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Body del Turno
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (shift.avatarInitials.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE5E7EB),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = shift.avatarInitials,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                    }

                    Column {
                        Text(
                            text = shift.clientOrTeam,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = shift.paymentDetail,
                            fontSize = 10.sp,
                            color = if (shift.paymentDetail.contains("Yape") || shift.paymentDetail.contains("Efectivo") || shift.paymentDetail.contains("Plin")) ForestGreen else TextSecondary
                        )
                    }
                }

                // Acciones
                when (shift.status) {
                    ShiftStatus.POR_VALIDAR -> {
                        Button(
                            onClick = onConfirmClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkForestGreen,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Confirmar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    ShiftStatus.TURNO_LIBRE -> {
                        OutlinedButton(
                            onClick = onAssignClick,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Bloquear / Asignar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                    ShiftStatus.EN_JUEGO -> {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Timer, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
