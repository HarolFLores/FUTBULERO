package com.incanatoapps.retofut.ui.screens.courts

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun CourtReservationsTab(
    viewModel: CourtManagementViewModel,
    onNewReservationClick: () -> Unit
) {
    val context = LocalContext.current
    val reservations = viewModel.getFilteredReservations()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ==========================================
        // 1. HEADER TITLE & NEW RESERVATION BUTTON
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Gestión de Reservas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )
                    Text(
                        text = "Control activo de clientes y cobros",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onNewReservationClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp).testTag("new_reservation_btn")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Nueva Reserva",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        // ==========================================
        // 2. SEARCH BAR
        // ==========================================
        item {
            OutlinedTextField(
                value = viewModel.reservationSearchQuery,
                onValueChange = { viewModel.reservationSearchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reservation_search_input"),
                placeholder = {
                    Text("Buscar por equipo o teléfono...", fontSize = 12.sp, color = TextSecondary)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (viewModel.reservationSearchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.reservationSearchQuery = "" }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Limpiar", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreen,
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        // ==========================================
        // 3. FILTER CHIPS
        // ==========================================
        item {
            val totalCount = viewModel.reservations.size
            val filterOptions = listOf("Todas ($totalCount)", "Confirmadas", "Señas Pendientes", "Bloqueadas")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterOptions.forEach { filterLabel ->
                    val isSelected = viewModel.reservationFilter == filterLabel ||
                            (filterLabel.startsWith("Todas") && viewModel.reservationFilter.startsWith("Todas"))
                    Surface(
                        onClick = { viewModel.reservationFilter = filterLabel },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) ForestGreen else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) ForestGreen else BorderGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filterLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. RESERVATION CARDS
        // ==========================================
        if (reservations.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.EventBusy, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No se encontraron reservas con este filtro", fontSize = 12.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(reservations, key = { it.id }) { reservation ->
                ReservationCard(
                    reservation = reservation,
                    onConfirmDeposit = {
                        viewModel.confirmReservationDeposit(reservation.id)
                        Toast.makeText(context, "Seña confirmada para ${reservation.clientName}", Toast.LENGTH_SHORT).show()
                    },
                    onCancelOrUnblock = {
                        viewModel.cancelOrUnblockReservation(reservation.id)
                        Toast.makeText(context, "Reserva cancelada / Turno liberado", Toast.LENGTH_SHORT).show()
                    },
                    onWhatsAppClick = {
                        val phone = reservation.phone ?: "974123456"
                        val cleanPhone = phone.replace("-", "").replace(" ", "")
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=51$cleanPhone&text=Hola%20${reservation.clientName},%20te%20escribimos%20de%20${viewModel.complexName}%20sobre%20tu%20turno.")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Abriendo WhatsApp para $phone", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReservationCard(
    reservation: CourtReservationUiModel,
    onConfirmDeposit: () -> Unit,
    onCancelOrUnblock: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Nombre + Badge Status + Fecha / Cancha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reservation.clientName,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(Modifier.width(6.dp))

                    Surface(
                        color = when (reservation.status) {
                            ReservationStatus.CONFIRMADA -> DarkForestGreen
                            ReservationStatus.SENA_PENDIENTE -> Color(0xFFFEF3C7)
                            ReservationStatus.BLOQUEADA -> Color(0xFFF3F4F6)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = reservation.status.label,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = when (reservation.status) {
                                ReservationStatus.CONFIRMADA -> LimeNeon
                                ReservationStatus.SENA_PENDIENTE -> Color(0xFFB45309)
                                ReservationStatus.BLOQUEADA -> Color(0xFF4B5563)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = reservation.dateTimeLabel,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }

            Spacer(Modifier.height(8.dp))

            // Row 2: Evento / Equipos vs Pago / Seña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.matchOrEvent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )
                Text(
                    text = reservation.paymentNote,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reservation.status == ReservationStatus.CONFIRMADA) ForestGreen else Color(0xFFB45309)
                )
            }

            Spacer(Modifier.height(4.dp))

            // Row 3: Subtítulo Cancha y Teléfono
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.courtFormat,
                    fontSize = 10.5.sp,
                    color = TextSecondary
                )
                if (reservation.phone != null) {
                    Text(
                        text = "Tel: ${reservation.phone}",
                        fontSize = 10.5.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Row 4: Botones de Acción (WhatsApp, Confirmar Seña, Cancelar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (reservation.phone != null) {
                    Surface(
                        onClick = onWhatsAppClick,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Chat, contentDescription = "WhatsApp", tint = ForestGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (reservation.status == ReservationStatus.SENA_PENDIENTE) {
                    Button(
                        onClick = onConfirmDeposit,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Confirmar Seña", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onCancelOrUnblock,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = if (reservation.status == ReservationStatus.SENA_PENDIENTE) Modifier.height(36.dp) else Modifier.weight(1f).height(36.dp)
                ) {
                    Text(
                        text = if (reservation.status == ReservationStatus.BLOQUEADA) "Desbloquear" else "Cancelar",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
