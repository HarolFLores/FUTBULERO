package com.incanatoapps.retofut.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun SoftMatchCard(
    teamA: String,
    teamB: String,
    time: String,
    location: String,
    status: String = "Próximo",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = CardWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header del Partido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInfo(teamA, "T1")
                
                Surface(
                    color = BrandGreenSoft.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "VS",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenDeep
                    )
                }

                TeamInfo(teamB, "T2")
            }
            
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = BorderGray.copy(alpha = 0.5f))
            Spacer(Modifier.height(20.dp))
            
            // Footer con Info y Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, null, Modifier.size(16.dp), TextSecondary)
                        Spacer(Modifier.width(6.dp))
                        Text(time, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Place, null, Modifier.size(16.dp), TextSecondary)
                        Spacer(Modifier.width(6.dp))
                        Text(location, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                
                Button(
                    onClick = { /* Join Logic */ },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
                ) {
                    Text("Unirme", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TeamInfo(name: String, shortName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AppBackground)
                .border(2.dp, BrandGreenSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(shortName, fontWeight = FontWeight.Black, color = BrandGreenDeep, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SoftSearchBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = TextSecondary)
            Spacer(Modifier.width(12.dp))
            Text("Buscar partidos o canchas...", color = TextSecondary, fontSize = 15.sp)
        }
    }
}

@Composable
fun SoftCategoryChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) BrandGreen else CardWhite,
        border = if (isSelected) null else BorderStroke(1.dp, BorderGray)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TextPrimary else TextSecondary
        )
    }
}

@Composable
fun DateSelector(selectedDate: String, onDateSelected: (String) -> Unit) {
    val dates = listOf("14", "15", "16", "17", "18", "19")
    val days = listOf("Mié", "Jue", "Vie", "Sáb", "Dom", "Lun")
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dates.size) { i ->
            val isSelected = dates[i] == selectedDate
            Surface(
                modifier = Modifier.width(64.dp).clickable { onDateSelected(dates[i]) },
                shape = RoundedCornerShape(24.dp),
                color = if (isSelected) BrandGreen else CardWhite,
                border = if (isSelected) null else BorderStroke(1.dp, BorderGray),
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(days[i], style = MaterialTheme.typography.labelMedium, color = if (isSelected) TextPrimary else TextSecondary)
                    Text(dates[i], style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
