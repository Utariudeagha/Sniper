package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Timeframe
import com.example.ui.components.CandlestickChart
import com.example.ui.components.TopdownMatrixView
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MarketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartDetailScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.SIGNALS)
    }

    val instrument by viewModel.selectedInstrument.collectAsState()
    val timeframe by viewModel.selectedTimeframe.collectAsState()
    val candles by viewModel.candles.collectAsState()
    val confluences by viewModel.confluenceMatrix.collectAsState()
    val signal by viewModel.selectedSignal.collectAsState()
    val watchlistSymbols by viewModel.watchlistSymbols.collectAsState()
    val isWatchlist = watchlistSymbols.contains(instrument.symbol)

    val confluenceScore = signal?.confluenceScore ?: 92

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar & Instrument Quote Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.SIGNALS) },
                        modifier = Modifier.testTag("chart_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = instrument.symbol,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
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
                            text = instrument.name,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
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
                        modifier = Modifier.testTag("toggle_watchlist_chart")
                    ) {
                        Icon(
                            imageVector = if (isWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Toggle Watchlist",
                            tint = if (isWatchlist) GoldLight else TextMuted
                        )
                    }
                }
            }
        }

        // Timeframe Selector Row (All 8 Timeframes)
        item {
            Surface(
                color = ObsidianCard,
                shape = RoundedCornerShape(12.dp),
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
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyanDark else ObsidianSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyanAccent else ObsidianCardBorder
                            ),
                            modifier = Modifier.testTag("tf_btn_${tf.code}")
                        ) {
                            Text(
                                text = tf.code,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Interactive Candlestick Chart with SMC & Sniper Lines
        item {
            CandlestickChart(
                instrument = instrument,
                candles = candles,
                activeSignal = signal,
                smcMarkers = signal?.smcMarkers ?: emptyList()
            )
        }

        // Action Buttons: Calculate Risk & Position Size
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.openRiskCalculator(signal) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calc_risk_chart_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = ObsidianBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Risk & Lot Size Calc",
                        color = ObsidianBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Top-Down Confluence Matrix (8 Timeframes Alignment)
        item {
            TopdownMatrixView(
                confluences = confluences,
                confluenceScore = confluenceScore,
                selectedTimeframe = timeframe,
                onSelectTimeframe = { viewModel.selectTimeframe(it) }
            )
        }

        // Institutional Manipulation & Market Maker Secrets Analysis
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CyanLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "MARKET MAKER SECRET & LIQUIDITY TRAP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = signal?.manipulationNote
                            ?: "Market makers engineering equal highs and retail liquidity traps before true expansion.",
                        fontSize = 13.sp,
                        color = TextWhite,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "HTF Narrative Bias:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Text(
                                text = signal?.htfBias ?: "Monthly/Weekly order flow driving expansion.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "LTF Sniper Trigger:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreenLight
                            )
                            Text(
                                text = signal?.ltfTrigger ?: "5M Change of Character (CHoCH) with Fair Value Gap retest.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Fundamental Context & Economic Calendar Clearance
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "FUNDAMENTAL CATALYST CLEARANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = signal?.fundamentalClearance
                            ?: "Safe: No high-impact red folder news in next 4 hours. Central bank policy aligns with technical bias.",
                        fontSize = 12.sp,
                        color = TextWhite,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
