package com.incanatoapps.retofut.ui.screens.guest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun GuestMatchTab(
    onViewCourtClick: (String, String) -> Unit,
    onRestrictedActionClick: () -> Unit
) {
    var selectedZone by remember { mutableStateOf("Todas las Zonas") }
    var selectedDate by remember { mutableStateOf("Hoy (Viernes)") }
    var isListView by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. LOCATION & TOGGLE LISTA / MAPA
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Chiclayo Central", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                }

                // Capsule Lista | Mapa
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3F4F6),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Surface(
                            onClick = { isListView = true },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isListView) DarkForestGreen else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.List, contentDescription = null, tint = if (isListView) LimeNeon else TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Lista", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isListView) LimeNeon else TextSecondary)
                            }
                        }

                        Surface(
                            onClick = { isListView = false },
                            shape = RoundedCornerShape(16.dp),
                            color = if (!isListView) DarkForestGreen else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Map, contentDescription = null, tint = if (!isListView) LimeNeon else TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Mapa", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (!isListView) LimeNeon else TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. ZONE CHIPS
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todas las Zonas", "Santa Victoria", "La Victoria").forEach { zone ->
                    val isSel = selectedZone == zone
                    Surface(
                        onClick = { selectedZone = zone },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) DarkForestGreen else Color(0xFFF3F4F6),
                        border = BorderStroke(1.dp, if (isSel) DarkForestGreen else BorderGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSel) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(LimeNeon))
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(
                                text = zone,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. DATE CHIPS
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("FECHA:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary)

                listOf("Hoy (Viernes)", "Mañana", "Fin de semana").forEach { dateLabel ->
                    val isSel = selectedDate == dateLabel
                    Surface(
                        onClick = { selectedDate = dateLabel },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) Color(0xFFDCFCE7) else Color.White,
                        border = BorderStroke(1.dp, if (isSel) Color(0xFF86EFAC) else BorderGray)
                    ) {
                        Text(
                            text = dateLabel,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) DarkForestGreen else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. HEADER: PICHANGAS DISPONIBLES
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pichangas Disponibles", fontSize = 14.5.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        color = LimeNeon,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "3 abiertas",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text("Actualizado en vivo", fontSize = 10.5.sp, color = TextSecondary)
            }
        }

        // ==========================================
        // 5. MATCH CARD 1: PICHANGA NOCTURNA EL OASIS
        // ==========================================
        item {
            GuestMatchCard(
                quotaPillText = "Faltan 2 Cupos",
                quotaPillBg = Color(0xFFFEE2E2),
                quotaPillColor = Color(0xFF991B1B),
                formatLocation = "Santa Victoria • F7 Sintético",
                price = "S/ 12.00",
                timeTag = "HOY 20:30",
                isTimeTagDark = true,
                title = "Pichanga Nocturna Complejo El Oasis",
                address = "📍 Cancha 2 Sintético • Santa Victoria, Chiclayo",
                avatars = listOf("C", "L", "+8"),
                levelBadge = "Nivel Intermedio",
                extraBadge = "Yape / Plin Aceptado",
                isExtraBadgeGreen = true,
                actionButtonText = "Inscribirse",
                onViewCourt = {
                    onViewCourtClick(
                        "Complejo Deportivo El Oasis",
                        "Calle Los Sauces 312, Urb. Santa Victoria • 3 canchas techadas con césped monofilamento y camerinos."
                    )
                },
                onAction = onRestrictedActionClick
            )
        }

        // ==========================================
        // 6. MATCH CARD 2: RETO LIBRE PATAZCA
        // ==========================================
        item {
            GuestMatchCard(
                quotaPillText = "Falta Arquero + 1 Def",
                quotaPillBg = Color(0xFFFEE2E2),
                quotaPillColor = Color(0xFF991B1B),
                formatLocation = "Patazca • Fútbol 6",
                price = "S/ 10.00",
                timeTag = "HOY 21:30",
                isTimeTagDark = true,
                title = "Reto Libre Canchas Patazca",
                address = "📍 Av. José Leonardo Ortiz 840, Urb. Patazca",
                avatars = listOf("D", "J", "+7"),
                levelBadge = "Nivel Competitivo",
                extraBadge = "⏱ Últimos 30 min",
                isExtraBadgeGreen = false,
                actionButtonText = "Unirme",
                onViewCourt = {
                    onViewCourtClick(
                        "Canchas Deportivas Patazca",
                        "Av. José Leonardo Ortiz 840, Urb. Patazca • 2 canchas de Fútbol 6 sintético con iluminación LED."
                    )
                },
                onAction = onRestrictedActionClick
            )
        }

        // ==========================================
        // 7. MATCH CARD 3: PICHANGA AMIGOS LA VICTORIA
        // ==========================================
        item {
            GuestMatchCard(
                quotaPillText = "Falta 1 Cupo",
                quotaPillBg = Color(0xFFDCFCE7),
                quotaPillColor = ForestGreen,
                formatLocation = "La Victoria • F7 Césped Natural",
                price = "S/ 15.00",
                timeTag = "SÁB 19:00",
                isTimeTagDark = false,
                title = "Pichanga Amigos La Victoria",
                address = "📍 Complejo El Triunfo, Av. Víctor Raúl Haya de la Torre",
                avatars = listOf("M", "+11"),
                levelBadge = "Nivel Recreativo / Amistoso",
                extraBadge = "Tercer Tiempo Incluido 🍻",
                isExtraBadgeGreen = true,
                actionButtonText = "Inscribirme",
                onViewCourt = {
                    onViewCourtClick(
                        "Complejo El Triunfo (La Victoria)",
                        "Av. Víctor Raúl Haya de la Torre, La Victoria • Cancha de césped natural con estacionamiento y cafetería."
                    )
                },
                onAction = onRestrictedActionClick
            )
        }
    }
}

@Composable
private fun GuestMatchCard(
    quotaPillText: String,
    quotaPillBg: Color,
    quotaPillColor: Color,
    formatLocation: String,
    price: String,
    timeTag: String,
    isTimeTagDark: Boolean,
    title: String,
    address: String,
    avatars: List<String>,
    levelBadge: String,
    extraBadge: String,
    isExtraBadgeGreen: Boolean,
    actionButtonText: String,
    onViewCourt: () -> Unit,
    onAction: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Quota pill + Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = quotaPillBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = quotaPillText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = quotaPillColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(formatLocation, fontSize = 10.5.sp, color = TextSecondary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(price, fontSize = 15.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    Text("por jugador", fontSize = 8.5.sp, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Body: Time tag + Title & Address
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9FAFB)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTimeTagDark) DarkForestGreen else Color(0xFFE5E7EB),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = timeTag.replace(" ", "\n"),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isTimeTagDark) LimeNeon else TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 11.sp
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                        Spacer(Modifier.height(2.dp))
                        Text(address, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Avatares + Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        avatars.forEach { av ->
                            Surface(
                                shape = CircleShape,
                                color = if (av.startsWith("+")) Color(0xFFE5E7EB) else DarkForestGreen,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = av,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (av.startsWith("+")) TextPrimary else LimeNeon
                                    )
                                }
                            }
                            Spacer(Modifier.width(2.dp))
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(levelBadge, fontSize = 9.5.sp, color = TextSecondary)
                }

                Surface(
                    color = if (isExtraBadgeGreen) Color(0xFFDCFCE7) else Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = extraBadge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExtraBadgeGreen) ForestGreen else Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botones: Ver Cancha & Inscribirse/Unirme con Candado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewCourt,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ver Cancha", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Button(
                    onClick = onAction,
                    modifier = Modifier.weight(1.2f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(actionButtonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}
