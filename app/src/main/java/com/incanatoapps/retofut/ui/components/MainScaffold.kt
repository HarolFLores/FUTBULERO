package com.incanatoapps.retofut.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.ui.screens.dashboard.FloatingPremiumBottomNav
import com.incanatoapps.retofut.ui.screens.dashboard.ModernDrawerContent
import com.incanatoapps.retofut.ui.theme.AppBackground
import com.incanatoapps.retofut.ui.theme.BorderGray
import com.incanatoapps.retofut.ui.theme.CardWhite
import com.incanatoapps.retofut.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModernDrawerContent(
                navController = navController,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = AppBackground,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            title.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardWhite)
                                .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Menu, "Abrir menú", tint = TextPrimary)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardWhite)
                                .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Notifications, null, Modifier.size(20.dp), tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AppBackground
                    )
                )
            },
            bottomBar = { FloatingPremiumBottomNav(navController) }
        ) { padding ->
            content(padding)
        }
    }
}
