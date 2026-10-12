package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AssetCategory
import com.example.model.SniperSignal
import com.example.ui.components.SniperSignalCard
import com.example.ui.theme.*
import com.example.viewmodel.MarketViewModel
import com.example.viewmodel.SignalsTab

@Composable
fun SignalsScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeSignalsTab.collectAsState()
    val activeSignals by viewModel.activeSignals.collectAsState()
    val potentialSignals by viewModel.potentialSignals.collectAsState()
    val completedSignals by viewModel.completedSignals.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val watchlistSymbols by viewModel.watchlistSymbols.collectAsState()

    var showOnlyWatchlist by remember { mutableStateOf(false) }

    // Filter signals by category and watchlist
    val filteredSignals = remember(activeTab, activeSignals, potentialSignals, completedSignals, selectedCategory, showOnlyWatchlist, watchlistSymbols) {
        val baseList = when (activeTab) {
            SignalsTab.ACTIVE_SNIPER -> activeSignals
            SignalsTab.POTENTIAL_SETUPS -> potentialSignals
            SignalsTab.COMPLETED_LEDGER -> completedSignals
        }
        baseList.filter { signal ->
            val matchesWatchlist = if (showOnlyWatchlist) watchlistSymbols.contains(signal.instrumentSymbol) else true
            val matchesCategory = if (selectedCategory == AssetCategory.ALL) true else {
                when (selectedCategory) {
                    AssetCategory.COMMODITIES -> signal.instrumentSymbol in listOf("XAUUSD", "SILVER", "USOIL")
                    AssetCategory.INDICES -> signal.instrumentSymbol in listOf("US100", "SPX500")
                    AssetCategory.CRYPTO -> signal.instrumentSymbol in listOf("BTCUSD")
                    AssetCategory.FOREX -> signal.instrumentSymbol !in listOf("XAUUSD", "SILVER", "USOIL", "US100", "SPX500", "BTCUSD")
                    else -> true
                }
            }
            matchesWatchlist && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Institutional Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(20.dp))
                    .testTag("hero_header_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Subtle background gradient & banner
                    Image(
                        painter = painterResource(id = R.drawable.img_forex_banner),
                        contentDescription = "Forex Terminal Art",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop,
                        alpha = 0.35f
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, ObsidianCard)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        // Live Session Status Badge
                        Surface(
                            color = BullishGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(BullishGreen)
                                )
                                Text(
                                    text = "PRIME KILLZONE: LONDON & NEW YORK OVERLAP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreenLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Institutional Sniper Confluence",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )

                        Text(
                            text = "50-Year Market Maker & Smart Money Top-Down Analysis Engine. Signals triggered only at 90%+ confluence.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Section Tabs: Active Sniper | Potential | Completed
        item {
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf(
                        Triple(SignalsTab.ACTIVE_SNIPER, "Sniper Calls", activeSignals.size),
                        Triple(SignalsTab.POTENTIAL_SETUPS, "Developing", potentialSignals.size),
                        Triple(SignalsTab.COMPLETED_LEDGER, "Completed", completedSignals.size)
                    )

                    tabs.forEach { (tab, label, count) ->
                        val isSelected = activeTab == tab
                        Surface(
                            onClick = { viewModel.setSignalsTab(tab) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_${tab.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ObsidianBackground else TextWhite
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = if (isSelected) ObsidianBackground.copy(alpha = 0.2f) else ObsidianSubtle,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "$count",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ObsidianBackground else GoldLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Chips & Watchlist Toggle
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Watchlist quick toggle pill
                    item {
                        FilterChip(
                            selected = showOnlyWatchlist,
                            onClick = { showOnlyWatchlist = !showOnlyWatchlist },
                            label = { Text("★ Watchlist Only") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (showOnlyWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (showOnlyWatchlist) GoldLight else TextMuted
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                                selectedLabelColor = GoldLight
                            ),
                            modifier = Modifier.testTag("filter_watchlist")
                        )
                    }

                    // Asset Categories
                    items(AssetCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanDark.copy(alpha = 0.3f),
                                selectedLabelColor = CyanLight
                            ),
                            modifier = Modifier.testTag("filter_cat_${category.name}")
                        )
                    }
                }
            }
        }

        // Strict Discipline Banner (Zero False Signals Guarantee)
        if (activeTab == SignalsTab.ACTIVE_SNIPER) {
            item {
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Strict High-Probability Filter (90%+ Win Rate Target)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "If no A+ setup exists across all 8 timeframes, no signal is issued. Capital preservation is the trader's ultimate edge.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Signals List or Empty State
        if (filteredSignals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NO HIGH PROBABILITY SETUP DETECTED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Market is in consolidation or high-impact news hazard. The system protects your capital until institutional liquidity purges.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredSignals, key = { it.id }) { signal ->
                SniperSignalCard(
                    signal = signal,
                    isWatchlist = watchlistSymbols.contains(signal.instrumentSymbol),
                    onToggleWatchlist = { viewModel.toggleWatchlist(signal.instrumentSymbol) },
                    onOpenChart = { viewModel.openChartForSignal(signal) },
                    onCalculateRisk = { viewModel.openRiskCalculator(signal) }
                )
            }
        }
    }
}
