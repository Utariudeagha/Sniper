package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssetCategory
import com.example.model.MarketInstrument
import com.example.model.SignalState
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MarketViewModel

@Composable
fun MarketScannerScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val instruments by viewModel.instruments.collectAsState()
    val activeSignals by viewModel.activeSignals.collectAsState()
    val potentialSignals by viewModel.potentialSignals.collectAsState()
    val watchlistSymbols by viewModel.watchlistSymbols.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<AssetCategory?>(null) }

    val filteredList = remember(instruments, searchQuery, selectedCat) {
        instruments.filter { inst ->
            val matchQuery = inst.symbol.contains(searchQuery, ignoreCase = true) ||
                    inst.name.contains(searchQuery, ignoreCase = true)
            val matchCat = if (selectedCat == null || selectedCat == AssetCategory.ALL) true else inst.category == selectedCat
            matchQuery && matchCat
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Scanner Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
                    .testTag("scanner_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MULTI-ASSET INSTITUTIONAL SCANNER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Real-Time Top-Down Setup Monitor",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Text(
                        text = "Scanning all 15 institutional instruments across 8 timeframes for structural alignment and liquidity sweeps.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search pair or commodity (e.g. XAUUSD, US100, GBP)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("scanner_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ObsidianCard,
                    unfocusedContainerColor = ObsidianCard,
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = ObsidianCardBorder
                )
            )
        }

        // Category Filter Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AssetCategory.values()) { category ->
                    val isSelected = selectedCat == category || (selectedCat == null && category == AssetCategory.ALL)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCat = if (category == AssetCategory.ALL) null else category },
                        label = { Text(category.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanDark.copy(alpha = 0.3f),
                            selectedLabelColor = CyanLight
                        ),
                        modifier = Modifier.testTag("scanner_cat_${category.name}")
                    )
                }
            }
        }

        // Table Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ASSET & FEED", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Text("PRICE • STATUS • BIAS", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }

        // Scanned Items List
        items(filteredList, key = { it.symbol }) { inst ->
            val activeSignal = activeSignals.find { it.instrumentSymbol == inst.symbol }
            val potentialSignal = potentialSignals.find { it.instrumentSymbol == inst.symbol }
            val isStarred = watchlistSymbols.contains(inst.symbol)

            val statusText = when {
                activeSignal != null -> "CONFIRMED SETUP (${activeSignal.action.name.replace("_", " ")})"
                potentialSignal != null -> "POTENTIAL DEVELOPING"
                else -> "NO TRADE (CONSOLIDATION)"
            }

            val statusColor = when {
                activeSignal != null -> BullishGreen
                potentialSignal != null -> GoldLight
                else -> TextMuted
            }

            Surface(
                onClick = {
                    viewModel.selectInstrument(inst)
                    viewModel.navigateTo(AppScreen.LIVE_CHARTS)
                },
                shape = RoundedCornerShape(12.dp),
                color = ObsidianCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (activeSignal != null) GoldPrimary.copy(alpha = 0.5f) else ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth().testTag("scan_row_${inst.symbol}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleWatchlist(inst.symbol) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (isStarred) GoldLight else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(inst.symbol, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(inst.name, fontSize = 11.sp, color = TextMuted)
                            Text("TV: ${inst.tradingViewSymbol}", fontSize = 9.sp, color = CyanLight)
                        }
                    }

                    // Right Column
                    Column(horizontalAlignment = Alignment.End) {
                        Text(inst.formatPrice(inst.currentPrice), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextWhite)

                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
