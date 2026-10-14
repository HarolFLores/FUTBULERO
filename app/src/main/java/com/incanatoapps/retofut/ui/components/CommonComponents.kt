package com.incanatoapps.retofut.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = onActionClick) {
            Text(actionText, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ChallengeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("RETO ABIERTO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text("Los Galácticos FC", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Fútbol 7 - Hoy 8:00 PM", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { /* TODO */ }, shape = RoundedCornerShape(8.dp)) {
                Text("Aceptar")
            }
        }
    }
}

@Composable
fun MatchCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cancha 'El Sol'", style = MaterialTheme.typography.labelMedium)
                Text("Mañana 7:00 PM", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Mi Equipo", fontWeight = FontWeight.Bold)
                Text(" VS ", modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                Text("Rival FC", fontWeight = FontWeight.Bold)
            }
        }
    }
}
