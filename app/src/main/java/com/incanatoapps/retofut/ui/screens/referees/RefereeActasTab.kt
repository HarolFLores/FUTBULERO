package com.incanatoapps.retofut.ui.screens.referees

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun RefereeActasTab(
    viewModel: RefereePortalViewModel,
    onFillActClick: (RefereeMatchActUiModel) -> Unit,
    onViewPdfClick: (RefereeMatchActUiModel) -> Unit
) {
    val filteredActs = viewModel.getFilteredActs()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ==========================================
        // 1. SEARCH BAR
        // ==========================================
        item {
            OutlinedTextField(
                value = viewModel.actasSearchQuery,
                onValueChange = { viewModel.actasSearchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("actas_search_input"),
                placeholder = {
                    Text("Buscar por equipo, torneo o árbitro...", fontSize = 12.sp, color = TextSecondary)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (viewModel.actasSearchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.actasSearchQuery = "" }) {
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
        // 2. FILTER CHIPS (Matching Images 2, 3, 4, 5)
        // ==========================================
        item {
            val filterOptions = listOf("Todas (14)", "Firmadas CODAR", "Pendientes", "Torneo Apertura")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterOptions.forEach { filterLabel ->
                    val isSelected = viewModel.actasFilter == filterLabel
                    Surface(
                        onClick = { viewModel.actasFilter = filterLabel },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) DarkForestGreen else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filterLabel,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) LimeNeon else TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. ACTAS LIST
        // ==========================================
        if (filteredActs.isEmpty()) {
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
                        Icon(Icons.Outlined.AssignmentLate, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No hay actas con el filtro seleccionado", fontSize = 12.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(filteredActs, key = { it.id }) { act ->
                MatchActCard(
                    act = act,
                    onFillClick = { onFillActClick(act) },
                    onViewPdfClick = { onViewPdfClick(act) }
                )
            }
        }
    }
}

@Composable
private fun MatchActCard(
    act: RefereeMatchActUiModel,
    onFillClick: () -> Unit,
    onViewPdfClick: () -> Unit
) {
    val isPending = act.status == MatchActStatus.PENDIENTE

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(if (isPending) 1.5.dp else 1.dp, if (isPending) ForestGreen else Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Status Badge + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isPending) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isPending) "● EN ESPERA DE INICIO" else "✔ FIRMADA CODAR • VALIDADA",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isPending) Color(0xFFB45309) else ForestGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = act.timeLabel,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = act.teamsMatch,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = act.tournamentAndVenue,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(8.dp))

            if (!isPending) {
                Text(
                    text = act.statsSummary,
                    fontSize = 10.5.sp,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            // Footer con Botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = act.actNumber,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen
                )

                if (isPending) {
                    Button(
                        onClick = onFillClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Outlined.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Llenar Acta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onViewPdfClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderGray),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ver PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}
