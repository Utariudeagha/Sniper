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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.EventImpact
import com.example.model.SignalState
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MarketViewModel

@Composable
fun DashboardScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val instruments by viewModel.instruments.collectAsState()
    val activeSignals by viewModel.activeSignals.collectAsState()
    val potentialSignals by viewModel.potentialSignals.collectAsState()
    val dataInfo by viewModel.dataProviderInfo.collectAsState()
    val events by viewModel.fundamentalEvents.collectAsState()
    val journalMetrics by viewModel.journalMetrics.collectAsState()
    val riskPct by viewModel.riskPercentPerTrade.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Terminal Card with TradingView Provider Badge
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(20.dp))
                    .testTag("dashboard_hero_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_forex_banner),
                        contentDescription = "Trading Banner",
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
                        // Data Provider Status Pill
                        Surface(
                            color = Color(dataInfo.status.badgeColorHex).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(dataInfo.status.badgeColorHex))
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
                                        .background(Color(dataInfo.status.badgeColorHex))
                                )
                                Text(
                                    text = dataInfo.getFormattedStatus(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(dataInfo.status.badgeColorHex)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Institutional Trading Terminal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )

                        Text(
                            text = "Real-time TradingView charts, deterministic top-down confluence engine, and strict capital preservation.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardQuickCard(
                    title = "Live Charts",
                    subtitle = "TradingView Embed",
                    icon = Icons.Default.ShowChart,
                    color = CyanLight,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.LIVE_CHARTS) }
                )
                DashboardQuickCard(
                    title = "Scanner",
                    subtitle = "15 Multi-Assets",
                    icon = Icons.Default.Search,
                    color = GoldLight,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.MARKET_SCANNER) }
                )
            }
        }

        // Market Overview / Top Assets Ticker Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MARKET OVERVIEW (REAL-TIME)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tap asset to chart",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(instruments.take(8)) { inst ->
                        Surface(
                            onClick = {
                                viewModel.selectInstrument(inst)
                                viewModel.navigateTo(AppScreen.LIVE_CHARTS)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = ObsidianCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = inst.symbol,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = inst.formatPrice(inst.currentPrice),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Surface(
                                    color = if (inst.change24hPercent >= 0) BullishGreen.copy(alpha = 0.2f) else BearishRed.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "${if (inst.change24hPercent >= 0) "+" else ""}${"%.2f".format(inst.change24hPercent)}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (inst.change24hPercent >= 0) BullishGreenLight else BearishRedLight,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Setups Spotlight
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HIGH-CONFLUENCE SIGNALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${activeSignals.size} Active • ${potentialSignals.size} Developing",
                            fontSize = 11.sp,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    activeSignals.firstOrNull()?.let { topSignal ->
                        Surface(
                            onClick = { viewModel.openChartForSignal(topSignal) },
                            shape = RoundedCornerShape(10.dp),
                            color = ObsidianSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${topSignal.action.name.replace("_", " ")}: ${topSignal.instrumentSymbol}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "${topSignal.confluenceScore}% Confluence",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BullishGreenLight
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Entry: ${"%.4f".format(topSignal.entryPrice).trimEnd('0').trimEnd('.')} • SL: ${"%.4f".format(topSignal.stopLossPrice).trimEnd('0').trimEnd('.')} • TP1 (1:3): ${"%.4f".format(topSignal.takeProfit1Price).trimEnd('0').trimEnd('.')}",
                                    fontSize = 12.sp,
                                    color = TextWhite
                                )

                                Text(
                                    text = topSignal.manipulationNote,
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.SIGNAL_CENTER) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View All Signal Plans & Journal", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Upcoming High-Impact Economic News
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ECONOMIC CALENDAR & NO-TRADE WINDOWS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    events.take(3).forEach { evt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ObsidianSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = evt.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Time: ${evt.scheduledTimeUtc} • Currency: ${evt.currency} • Source: ${evt.dataSource}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }

                                Surface(
                                    color = if (evt.impact == EventImpact.HIGH) BearishRed.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = evt.impact.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (evt.impact == EventImpact.HIGH) BearishRedLight else GoldLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Journal Performance Summary
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TRANSPARENT PERFORMANCE LEDGER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreenLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PerformancePill("Win Rate", "${journalMetrics.winRatePercent}%", BullishGreenLight)
                        PerformancePill("Profit Factor", "${journalMetrics.profitFactor}", GoldLight)
                        PerformancePill("Expectancy", "+${journalMetrics.realizedRExpectancy} R", CyanLight)
                        PerformancePill("Max DD", "${journalMetrics.maxDrawdownPercent}%", TextMuted)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "🛡️ Transparent standard: All losing trades remain recorded. Configured risk per trade: $riskPct% of equity.",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardQuickCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun PerformancePill(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
