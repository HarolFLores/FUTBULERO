package com.incanatoapps.retofut.ui.screens.referees

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.ui.theme.*

data class RefereeCardItem(
    val id: Int,
    val name: String,
    val price: String,
    val categoryBadge: String,
    val rating: String,
    val matchesCount: Int,
    val availabilityChip: String,
    val specialtyChip: String,
    val venueInfo: String,
    val initials: String = "AR"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefereeDirectoryScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val filterOptions = listOf("• Todos (12)", "Disponibles Hoy", "Fútbol 7 & 8", "Fútbol 11")

    val referees = remember {
        listOf(
            RefereeCardItem(
                id = 1,
                name = "Juan Carlos Pérez",
                price = "S/. 35.00",
                categoryBadge = "ÁRBITRO PRINCIPAL FIFA AMATEUR",
                rating = "4.9",
                matchesCount = 84,
                availabilityChip = "🟢 Disponible Viernes y Sábado",
                specialtyChip = "🕒 Puntualidad 100%",
                venueInfo = "Colegiado CODAR Chiclayo • Sede Centro y La Victoria",
                initials = "JP"
            ),
            RefereeCardItem(
                id = 2,
                name = "Roberto \"Cholo\" Miranda",
                price = "S/. 40.00",
                categoryBadge = "COLEGIO DE ÁRBITROS CHICLAYO",
                rating = "4.8",
                matchesCount = 112,
                availabilityChip = "🟢 Disponible Hoy noche (Desde 19:00)",
                specialtyChip = "🏆 Especialista F7 / F8",
                venueInfo = "Torneo La Victoria • Parque Infantil Chiclayo",
                initials = "RM"
            ),
            RefereeCardItem(
                id = 3,
                name = "Marcos Delgado S.",
                price = "S/. 30.00",
                categoryBadge = "LIGA DISTRITAL PIMENTEL",
                rating = "4.7",
                matchesCount = 46,
                availabilityChip = "🟢 Disponible Fin de Semana",
                specialtyChip = "⚽ Fútbol Rápido & Sintético",
                venueInfo = "Pimentel, Reque y Santa Victoria",
                initials = "MD"
            )
        )
    }

    val filteredReferees = remember(searchQuery, selectedFilterIndex, referees) {
        referees.filter { ref ->
            val matchesQuery = searchQuery.isBlank() || ref.name.contains(searchQuery, ignoreCase = true) || ref.venueInfo.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilterIndex) {
                1 -> ref.availabilityChip.contains("Hoy", ignoreCase = true)
                2 -> ref.specialtyChip.contains("F7", ignoreCase = true) || ref.specialtyChip.contains("F8", ignoreCase = true)
                3 -> ref.specialtyChip.contains("11", ignoreCase = true) || ref.categoryBadge.contains("FIFA", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkForestGreen)
                    .statusBarsPadding()
            ) {
                // Top Bar: Back, Title, Avatar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Árbitros Y Vocales",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = { navController.navigate("player_card/1") }) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ForestGreen)
                                .border(1.dp, LimeNeon, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Subtitle Header
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LimeNeon))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "COLEGIO OFICIAL CODAR",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = LimeNeon,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Cuerpo Arbitral Federado",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Árbitros Oficiales en Chiclayo & Lambayeque",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Search Bar + Filter Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Buscar árbitro por nombre o colegio...", fontSize = 12.sp, color = TextSecondary)
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("referee_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = LimeNeon,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.width(8.dp))

                    Surface(
                        onClick = {
                            Toast.makeText(context, "Filtros avanzados de arbitraje", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = LimeNeon,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Tune, null, tint = DarkForestGreen, modifier = Modifier.size(22.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "24 partidos arbitrados este fin de semana en Chiclayo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("📈", fontSize = 12.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. FILTROS HORIZONTALES
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEachIndexed { idx, filterTitle ->
                        val isSel = selectedFilterIndex == idx
                        Surface(
                            onClick = { selectedFilterIndex = idx },
                            shape = RoundedCornerShape(50),
                            color = if (isSel) ForestGreen else Color.White,
                            border = BorderStroke(1.dp, if (isSel) ForestGreen else BorderGray),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                                Text(
                                    text = filterTitle,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 2. BANNER DE GARANTÍA CHOCOFULBO
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Sports, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Garantía ChocoFulbo",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(color = ForestGreen, shape = RoundedCornerShape(50)) {
                                    Text(
                                        text = "100% Certificados",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Árbitros con carnet oficial CODAR Lambayeque y calificación verificada por capitanes de Chiclayo.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 3. LISTA DE ÁRBITROS FEDERADOS
            items(filteredReferees, key = { it.id }) { ref ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Avatar con insignia verificada
                            Box(modifier = Modifier.size(54.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Sports, null, tint = DarkForestGreen, modifier = Modifier.size(28.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(ForestGreen)
                                        .border(1.5.dp, Color.White, CircleShape)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Check, null, tint = LimeNeon, modifier = Modifier.size(11.dp))
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ref.name,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = ref.price,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )
                                }

                                Spacer(Modifier.height(2.dp))

                                Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = ref.categoryBadge,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Star, null, tint = Color(0xFFEAB308), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "${ref.rating} ",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "(${ref.matchesCount} partidos dirigidos)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Chips de Disponibilidad y Especialidad
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(50)) {
                                Text(
                                    text = ref.availabilityChip,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(50)) {
                                Text(
                                    text = ref.specialtyChip,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Shield, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = ref.venueInfo,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Botón Contratar
                        Button(
                            onClick = {
                                Toast.makeText(context, "¡Árbitro ${ref.name} solicitado para tu próximo partido! Tarifa acordada: ${ref.price}", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                text = "Contratar para Partido",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}
