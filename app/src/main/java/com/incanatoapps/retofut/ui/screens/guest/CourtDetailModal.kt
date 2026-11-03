package com.incanatoapps.retofut.ui.screens.guest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun CourtDetailModal(
    complexName: String = "Complejo Deportivo El Oasis",
    address: String = "Calle Los Sauces 312, Urb. Santa Victoria • 3 canchas techadas con césped monofilamento y camerinos.",
    onDismiss: () -> Unit,
    onRequireAccountClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("court_detail_modal")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header con botón cerrar X
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "COMPLEJO DEPORTIVO",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = complexName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen
                        )
                    }

                    Surface(
                        onClick = onDismiss,
                        shape = CircleShape,
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = address,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(Modifier.height(14.dp))

                // Grilla 2x2 de Equipamiento / Amenities
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF9FAFB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AmenityItem(icon = "🅿", text = "Estacionamiento")
                            AmenityItem(icon = "🚿", text = "Vestuarios & Ducha")
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AmenityItem(icon = "🔆", text = "Iluminación LED")
                            AmenityItem(icon = "📶", text = "Wi-Fi Libre")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Botón verde CTA
                Button(
                    onClick = {
                        onDismiss()
                        onRequireAccountClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("reserve_schedule_guest_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeNeon,
                        contentColor = DarkForestGreen
                    )
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "📅  Reservar Horario (Requiere Cuenta)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun AmenityItem(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 14.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
