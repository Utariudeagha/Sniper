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
import com.example.model.SignalState
import com.example.model.SniperSignal
import com.example.model.TradeAction
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
    val noTradeItems by viewModel.noTradeSignals.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val watchlistSymbols by viewModel.watchlistSymbols.collectAsState()

    var showOnlyWatchlist by remember { mutableStateOf(false) }

    val filteredSignals = remember(activeTab, activeSignals, potentialSignals, completedSignals, noTradeItems, selectedCategory, showOnlyWatchlist, watchlistSymbols) {
        val baseList = when (activeTab) {
            SignalsTab.CONFIRMED_SETUPS -> activeSignals
            SignalsTab.POTENTIAL_SETUPS -> potentialSignals
            SignalsTab.NO_TRADE_STATUS -> noTradeItems
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
        // Hero Header Card
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

                    Column(modifier = Modifier.padding(16.dp)) {
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
                                    text = "PRIME KILLZONE • DETERMINISTIC SIGNALS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreenLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Signal Center & Trade Plans",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )

                        Text(
                            text = "Conditioned strictly on closed candles and 1:3 to 1:5 RR. If criteria fail, NO TRADE is displayed to preserve capital.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Section Tabs: Confirmed | Potential | No Trade | Completed
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
                        Triple(SignalsTab.CONFIRMED_SETUPS, "Confirmed", activeSignals.size),
                        Triple(SignalsTab.POTENTIAL_SETUPS, "Developing", potentialSignals.size),
                        Triple(SignalsTab.NO_TRADE_STATUS, "No Trade", noTradeItems.size),
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
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ObsidianBackground else TextWhite
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Surface(
                                    color = if (isSelected) ObsidianBackground.copy(alpha = 0.2f) else ObsidianSubtle,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "$count",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ObsidianBackground else GoldLight,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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

        // Capital Preservation Notice when in No Trade Tab
        if (activeTab == SignalsTab.NO_TRADE_STATUS) {
            item {
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
                            Text("DISCIPLINED CAPITAL PRESERVATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                        }
                        Text(
                            text = "These pairs were algorithmically scanned and failed high-probability criteria (e.g. choppy consolidation, high spread, or scheduled news risk). No trade is permitted until conditions resolve.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Signals List
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
                            text = "NO QUALIFYING SETUPS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Market conditions do not currently provide a verified 1:3 to 1:5 setup. Capital remains protected.",
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
