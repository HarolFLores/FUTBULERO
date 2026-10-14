package com.incanatoapps.retofut.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incanatoapps.retofut.ui.components.ChallengeCard
import com.incanatoapps.retofut.ui.components.MatchCard
import com.incanatoapps.retofut.ui.components.SectionHeader
import com.incanatoapps.retofut.ui.components.StatItem

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "¡Hola, Capitán!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Text(
                            "MI CARD",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            "88",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "OVR",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatItem("RIT", "85")
                        StatItem("TIR", "82")
                        StatItem("PAS", "90")
                    }
                }
            }
        }

        item {
            SectionHeader("Retos Activos", "Ver todos")
        }

        items(2) {
            ChallengeCard()
        }

        item {
            SectionHeader("Próximos Partidos", "Ver agenda")
        }

        items(2) {
            MatchCard()
        }
    }
}
