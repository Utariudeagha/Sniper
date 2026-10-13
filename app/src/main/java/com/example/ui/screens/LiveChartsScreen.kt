package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Timeframe
import com.example.ui.components.CandlestickChart
import com.example.ui.components.TopdownMatrixView
import com.example.ui.components.TradingViewChart
import com.example.ui.theme.*
import com.example.viewmodel.ChartDisplayMode
import com.example.viewmodel.MarketViewModel

@Composable
fun LiveChartsScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val instrument by viewModel.selectedInstrument.collectAsState()
    val timeframe by viewModel.selectedTimeframe.collectAsState()
    val candles by viewModel.candles.collectAsState()
    val confluences by viewModel.confluenceMatrix.collectAsState()
    val signal by viewModel.selectedSignal.collectAsState()
    val chartMode by viewModel.chartDisplayMode.collectAsState()
    val instruments by viewModel.instruments.collectAsState()
    val watchlistSymbols by viewModel.watchlistSymbols.collectAsState()
    val isWatchlist = watchlistSymbols.contains(instrument.symbol)

    val confluenceScore = signal?.confluenceScore ?: 90

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Instrument Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = instrument.symbol,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Surface(
                            color = if (instrument.change24hPercent >= 0) BullishGreen.copy(alpha = 0.2f) else BearishRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
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
                    Text(
                        text = "${instrument.name} • TV: ${instrument.tradingViewSymbol}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = instrument.formatPrice(instrument.currentPrice),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldLight
                        )
                        Text(
                            text = "Spread: ${instrument.spreadPips} pips",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleWatchlist(instrument.symbol) },
                        modifier = Modifier.testTag("toggle_watchlist_live_chart")
                    ) {
                        Icon(
                            imageVector = if (isWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatchlist) GoldLight else TextMuted
                        )
                    }
                }
            }
        }

        // Quick Pair Switcher Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(instruments) { item ->
                    val isSelected = item.symbol == instrument.symbol
                    Surface(
                        onClick = { viewModel.selectInstrument(item) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GoldPrimary else ObsidianCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldLight else ObsidianCardBorder),
                        modifier = Modifier.testTag("quick_pair_${item.symbol}")
                    ) {
                        Text(
                            text = item.symbol,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ObsidianBackground else TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Mode Switcher: TradingView Advanced Embed vs Native SMC Canvas
        item {
            Surface(
                color = ObsidianCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val isTv = chartMode == ChartDisplayMode.TRADINGVIEW_ADVANCED
                    Surface(
                        onClick = { if (!isTv) viewModel.toggleChartDisplayMode() },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTv) CyanDark else androidx.compose.ui.graphics.Color.Transparent,
                        modifier = Modifier.weight(1f).testTag("mode_tv")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, tint = if (isTv) TextWhite else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TradingView Real-Time", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isTv) TextWhite else TextMuted)
                        }
                    }

                    Surface(
                        onClick = { if (isTv) viewModel.toggleChartDisplayMode() },
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isTv) GoldPrimary else androidx.compose.ui.graphics.Color.Transparent,
                        modifier = Modifier.weight(1f).testTag("mode_smc")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = if (!isTv) ObsidianBackground else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Smart Money Overlays", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isTv) ObsidianBackground else TextMuted)
                        }
                    }
                }
            }
        }

        // Timeframe Selector Row (All 8 Timeframes)
        item {
            Surface(
                color = ObsidianCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(Timeframe.values()) { tf ->
                        val isSelected = tf == timeframe
                        Surface(
                            onClick = { viewModel.selectTimeframe(tf) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) CyanDark else ObsidianSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyanAccent else ObsidianCardBorder
                            ),
                            modifier = Modifier.testTag("tf_tab_${tf.code}")
                        ) {
                            Text(
                                text = tf.code,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Chart View (TradingView or Native SMC)
        item {
            if (chartMode == ChartDisplayMode.TRADINGVIEW_ADVANCED) {
                TradingViewChart(
                    instrument = instrument,
                    timeframe = timeframe
                )
            } else {
                CandlestickChart(
                    instrument = instrument,
                    candles = candles,
                    activeSignal = signal,
                    smcMarkers = signal?.smcMarkers ?: emptyList()
                )
            }
        }

        // Quick Risk Calculator Trigger
        item {
            Button(
                onClick = { viewModel.openRiskCalculator(signal) },
                modifier = Modifier.fillMaxWidth().testTag("open_calc_from_charts"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Institutional Position & Risk Calculator", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Top-Down Confluence Matrix
        item {
            TopdownMatrixView(
                confluences = confluences,
                confluenceScore = confluenceScore,
                selectedTimeframe = timeframe,
                onSelectTimeframe = { viewModel.selectTimeframe(it) }
            )
        }

        // Structural Insights Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier.fillMaxWidth().border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanLight, modifier = Modifier.size(18.dp))
                        Text("MARKET STRUCTURE & CONFLUENCE CONTEXT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanLight, letterSpacing = 1.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = signal?.manipulationNote ?: "Market makers engineering equal highs and retail liquidity traps before true expansion.",
                        fontSize = 12.sp,
                        color = TextWhite,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("HTF Narrative:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                            Text(signal?.htfBias ?: "Macro order flow driving expansion.", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("LTF Confirmation:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BullishGreenLight)
                            Text(signal?.ltfTrigger ?: "5M Change of Character (CHoCH) with Fair Value Gap retest.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
