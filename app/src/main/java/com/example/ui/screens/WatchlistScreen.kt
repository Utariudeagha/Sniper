package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssetCategory
import com.example.model.MarketInstrument
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MarketViewModel

@Composable
fun WatchlistScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val instruments by viewModel.instruments.collectAsState()
    val watchlistSymbols by viewModel.watchlistSymbols.collectAsState()
    var selectedCategory by remember { mutableStateOf<AssetCategory?>(null) }
    var filterStarredOnly by remember { mutableStateOf(false) }

    val filteredInstruments = remember(instruments, watchlistSymbols, selectedCategory, filterStarredOnly) {
        instruments.filter { inst ->
            val matchesStarred = if (filterStarredOnly) watchlistSymbols.contains(inst.symbol) else true
            val matchesCat = if (selectedCategory == null || selectedCategory == AssetCategory.ALL) true else inst.category == selectedCategory
            matchesStarred && matchesCat
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
        // Institutional Market Sessions Bar
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                    .testTag("sessions_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GLOBAL INTERBANK TRADING SESSIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SessionBadge("LONDON", "OPEN", BullishGreen)
                        SessionBadge("NEW YORK", "OPEN", BullishGreen)
                        SessionBadge("TOKYO", "CLOSED", TextMuted)
                        SessionBadge("SYDNEY", "CLOSED", TextMuted)
                    }
                }
            }
        }

        // Filter Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = filterStarredOnly,
                        onClick = { filterStarredOnly = !filterStarredOnly },
                        label = { Text("★ Starred (${watchlistSymbols.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                            selectedLabelColor = GoldLight
                        ),
                        modifier = Modifier.testTag("filter_starred")
                    )
                }

                items(AssetCategory.values()) { category ->
                    val isSelected = selectedCategory == category || (selectedCategory == null && category == AssetCategory.ALL)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = if (category == AssetCategory.ALL) null else category },
                        label = { Text(category.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanDark.copy(alpha = 0.3f),
                            selectedLabelColor = CyanLight
                        ),
                        modifier = Modifier.testTag("cat_chip_${category.name}")
                    )
                }
            }
        }

        // Instrument List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ASSET / NAME", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Text("LIVE QUOTE / 24H", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }

        // Instruments Items
        items(filteredInstruments, key = { it.symbol }) { inst ->
            val isStarred = watchlistSymbols.contains(inst.symbol)
            InstrumentWatchlistRow(
                instrument = inst,
                isStarred = isStarred,
                onToggleStar = { viewModel.toggleWatchlist(inst.symbol) },
                onClick = {
                    viewModel.selectInstrument(inst)
                    viewModel.navigateTo(AppScreen.LIVE_CHARTS)
                }
            )
        }
    }
}

@Composable
fun InstrumentWatchlistRow(
    instrument: MarketInstrument,
    isStarred: Boolean,
    onToggleStar: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("watchlist_row_${instrument.symbol}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Star + Symbol + Category
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier.size(32.dp).testTag("star_${instrument.symbol}")
                ) {
                    Icon(
                        imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star",
                        tint = if (isStarred) GoldLight else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = instrument.symbol,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = instrument.name,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Spread: ${instrument.spreadPips} pips",
                        fontSize = 9.sp,
                        color = CyanLight
                    )
                }
            }

            // Right: Live Price + 24H Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = instrument.formatPrice(instrument.currentPrice),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )

                Surface(
                    color = if (instrument.change24hPercent >= 0) BullishGreen.copy(alpha = 0.2f) else BearishRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${if (instrument.change24hPercent >= 0) "+" else ""}${"%.2f".format(instrument.change24hPercent)}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (instrument.change24hPercent >= 0) BullishGreenLight else BearishRedLight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SessionBadge(name: String, status: String, statusColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Text(text = status, fontSize = 9.sp, color = statusColor, fontWeight = FontWeight.Bold)
        }
    }
}
