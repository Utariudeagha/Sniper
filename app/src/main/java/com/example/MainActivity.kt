package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.RiskCalculatorDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MarketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SniperFxApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SniperFxApp(
    viewModel: MarketViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val riskDialogSignal by viewModel.riskDialogSignal.collectAsState()

    // Risk Calculator Dialog
    if (riskDialogSignal != null) {
        RiskCalculatorDialog(
            signal = riskDialogSignal,
            onDismiss = { viewModel.closeRiskCalculator() }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = GoldPrimary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TrackChanges,
                                    contentDescription = null,
                                    tint = ObsidianBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SNIPER",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "FX",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "50-Year Institutional Confluence",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                },
                actions = {
                    // Institutional Mode Pill
                    Surface(
                        color = CyanDark.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BullishGreen)
                            )
                            Text(
                                text = "A+ ONLY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanLight
                            )
                        }
                    }

                    // Quick Risk Calculator Trigger
                    IconButton(
                        onClick = { viewModel.openRiskCalculator(null) },
                        modifier = Modifier.testTag("appbar_calc_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Risk Calculator",
                            tint = GoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = ObsidianCardBorder,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .testTag("main_bottom_nav")
            ) {
                val navItems = listOf(
                    NavItem(AppScreen.SIGNALS, "Signals", Icons.Default.Bolt),
                    NavItem(AppScreen.CHART_DETAIL, "Top-Down", Icons.Default.ShowChart),
                    NavItem(AppScreen.WATCHLIST, "Watchlist", Icons.Default.Star),
                    NavItem(AppScreen.ACADEMY, "Academy", Icons.Default.School),
                    NavItem(AppScreen.RISK_CALCULATOR, "Risk Math", Icons.Default.Shield)
                )

                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBackground,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${item.screen.name}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBackground)
        ) {
            when (currentScreen) {
                AppScreen.SIGNALS -> SignalsScreen(viewModel = viewModel)
                AppScreen.CHART_DETAIL -> ChartDetailScreen(viewModel = viewModel)
                AppScreen.WATCHLIST -> WatchlistScreen(viewModel = viewModel)
                AppScreen.ACADEMY -> EducationScreen()
                AppScreen.RISK_CALCULATOR -> RiskDisciplineScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)
