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

        // Real-Time TradingView Signal & Institutional Execution HUD
        item {
            val curSignal = signal
            if (curSignal != null) {
                val isBuy = curSignal.action == com.example.model.TradeAction.BUY_SNIPER
                val isNoTrade = curSignal.action == com.example.model.TradeAction.NO_TRADE
                val isPotential = curSignal.state == com.example.model.SignalState.POTENTIAL_DEVELOPING

                val bannerColor = when {
                    isNoTrade -> GoldPrimary
                    isPotential -> CyanLight
                    isBuy -> BullishGreen
                    else -> BearishRed
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (curSignal.confluenceScore >= 90) GoldPrimary.copy(alpha = 0.5f) else ObsidianCardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("tv_signal_hud_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Signal Status & Confluence Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = bannerColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, bannerColor.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isNoTrade -> Icons.Default.Shield
                                            isPotential -> Icons.Default.HourglassBottom
                                            isBuy -> Icons.Default.ArrowOutward
                                            else -> Icons.Default.SouthEast
                                        },
                                        contentDescription = null,
                                        tint = bannerColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = when {
                                            isNoTrade -> "NO TRADE • CAPITAL PROTECTED"
                                            isPotential -> "DEVELOPING SMC SETUP"
                                            isBuy -> "CONFIRMED BUY SNIPER"
                                            else -> "CONFIRMED SELL SNIPER"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = bannerColor
                                    )
                                }
                            }

                            Surface(
                                color = if (curSignal.confluenceScore >= 90) GoldPrimary.copy(alpha = 0.2f) else CyanDark.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${curSignal.confluenceScore}% Confluence",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (curSignal.confluenceScore >= 90) GoldLight else CyanLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${curSignal.instrumentSymbol} • ${curSignal.instrumentName}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Conditioned on closed candles • ${curSignal.riskRewardRatio} Risk:Reward ratio",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Price Levels Grid: Entry | SL | TP1 (1:3) | TP2 (1:5)
                        if (!isNoTrade) {
                            Surface(
                                color = ObsidianSurface,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("SNIPER ENTRY", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = instrument.formatPrice(curSignal.entryPrice),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TextWhite
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("STOP LOSS (INVALIDATION)", fontSize = 10.sp, color = BearishRedLight, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = "${instrument.formatPrice(curSignal.stopLossPrice)} (-${curSignal.riskPips} p)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BearishRedLight
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = ObsidianCardBorder
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("TAKE PROFIT 1 (1:3 RR)", fontSize = 10.sp, color = BullishGreenLight, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = "${instrument.formatPrice(curSignal.takeProfit1Price)} (+${curSignal.tp1Pips} p)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BullishGreenLight
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("TAKE PROFIT 2 (1:5 RR)", fontSize = 10.sp, color = GoldLight, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = "${instrument.formatPrice(curSignal.takeProfit2Price)} (+${curSignal.tp2Pips} p)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldLight
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // SMC & ICT Technical Narrative Deep Dive
                        Surface(
                            color = ObsidianSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanLight, modifier = Modifier.size(16.dp))
                                    Text("INSTITUTIONAL ORDER FLOW & SMC MECHANICS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanLight, letterSpacing = 0.5.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Higher-Timeframe (HTF) Macro Bias:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                                Text(curSignal.htfBias, fontSize = 11.sp, color = TextWhite, lineHeight = 16.sp)

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Lower-Timeframe (LTF) Execution Trigger:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BullishGreenLight)
                                Text(curSignal.ltfTrigger, fontSize = 11.sp, color = TextWhite, lineHeight = 16.sp)

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Market Maker Trap / Manipulation Note:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BearishRedLight)
                                Text(curSignal.manipulationNote, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Fundamental Clearance:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanLight)
                                Text(curSignal.fundamentalClearance, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Strict Invalidation Condition:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                Text(curSignal.invalidationConditions, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                            }
                        }

                        // Smart Money Markers Chips if present
                        if (curSignal.smcMarkers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Active Institutional Zones on Chart:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(curSignal.smcMarkers) { marker ->
                                    Surface(
                                        color = ObsidianCardBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${marker.label} (${"%.2f".format(marker.startPrice)}-${"%.2f".format(marker.endPrice)})",
                                            fontSize = 10.sp,
                                            color = GoldLight,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
    }
}
