package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val dataInfo by viewModel.dataProviderInfo.collectAsState()

    var showHubDrawer by remember { mutableStateOf(false) }

    // Handle back button on sub-screens
    if (currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.DASHBOARD) }
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
                                text = currentScreen.title,
                                fontSize = 10.sp,
                                color = CyanLight
                            )
                        }
                    }
                },
                actions = {
                    // Feed Status Pill
                    Surface(
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        color = Color(dataInfo.status.badgeColorHex).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(dataInfo.status.badgeColorHex)),
                        modifier = Modifier.padding(end = 4.dp).testTag("feed_status_pill")
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
                                    .background(Color(dataInfo.status.badgeColorHex))
                            )
                            Text(
                                text = "TV LIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(dataInfo.status.badgeColorHex)
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

                    // All Pages Navigation Hub Menu
                    IconButton(
                        onClick = { showHubDrawer = !showHubDrawer },
                        modifier = Modifier.testTag("appbar_hub_menu")
                    ) {
                        Icon(
                            imageVector = if (showHubDrawer) Icons.Default.Close else Icons.Default.GridView,
                            contentDescription = "Page Hub",
                            tint = TextWhite
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
                val primaryNavItems = listOf(
                    NavItem(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                    NavItem(AppScreen.LIVE_CHARTS, "Charts", Icons.Default.ShowChart),
                    NavItem(AppScreen.MARKET_SCANNER, "Scanner", Icons.Default.Search),
                    NavItem(AppScreen.SIGNAL_CENTER, "Signals", Icons.Default.Bolt),
                    NavItem(AppScreen.SIGNAL_JOURNAL, "Journal", Icons.Default.Assessment)
                )

                primaryNavItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            showHubDrawer = false
                            viewModel.navigateTo(item.screen)
                        },
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
            // Main content
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppScreen.LIVE_CHARTS -> LiveChartsScreen(viewModel = viewModel)
                AppScreen.MARKET_SCANNER -> MarketScannerScreen(viewModel = viewModel)
                AppScreen.SIGNAL_CENTER -> SignalsScreen(viewModel = viewModel)
                AppScreen.WATCHLIST -> WatchlistScreen(viewModel = viewModel)
                AppScreen.SIGNAL_JOURNAL -> SignalJournalScreen(viewModel = viewModel)
                AppScreen.ACADEMY -> EducationScreen()
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }

            // Quick Hub Drawer Overlay (giving 1-tap jump to any of the 8 pages)
            if (showHubDrawer) {
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.TopCenter)
                        .testTag("page_hub_overlay")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ALL TERMINAL MODULES (8 PAGES)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight,
                                letterSpacing = 1.sp
                            )
                            IconButton(onClick = { showHubDrawer = false }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val allPages = listOf(
                            Triple(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                            Triple(AppScreen.LIVE_CHARTS, "Live Charts (TradingView)", Icons.Default.ShowChart),
                            Triple(AppScreen.MARKET_SCANNER, "Market Scanner", Icons.Default.Search),
                            Triple(AppScreen.SIGNAL_CENTER, "Signal Center", Icons.Default.Bolt),
                            Triple(AppScreen.WATCHLIST, "Watchlist", Icons.Default.Star),
                            Triple(AppScreen.SIGNAL_JOURNAL, "Signal Journal & Stats", Icons.Default.Assessment),
                            Triple(AppScreen.ACADEMY, "Trading Academy", Icons.Default.School),
                            Triple(AppScreen.SETTINGS, "Data Status & Settings", Icons.Default.Settings)
                        )

                        allPages.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { (screen, title, icon) ->
                                    val isCurrent = currentScreen == screen
                                    Surface(
                                        onClick = {
                                            viewModel.navigateTo(screen)
                                            showHubDrawer = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isCurrent) GoldDark.copy(alpha = 0.3f) else ObsidianSurface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isCurrent) GoldPrimary else ObsidianCardBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isCurrent) GoldLight else CyanLight,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isCurrent) GoldLight else TextWhite
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)
