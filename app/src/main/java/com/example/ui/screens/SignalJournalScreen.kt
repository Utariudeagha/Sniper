package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.SignalState
import com.example.model.SniperSignal
import com.example.model.TradeAction
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MarketViewModel

@Composable
fun SignalJournalScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val completedSignals by viewModel.completedSignals.collectAsState()
    val metrics by viewModel.journalMetrics.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Journal Statistical Overview Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("journal_metrics_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PERSISTENT SIGNAL JOURNAL & PERFORMANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("WIN RATE", "${metrics.winRatePercent}%", BullishGreenLight)
                        MetricBox("PROFIT FACTOR", "${metrics.profitFactor}", GoldLight)
                        MetricBox("EXPECTANCY", "+${metrics.realizedRExpectancy} R", CyanLight)
                        MetricBox("MAX DRAWDOWN", "${metrics.maxDrawdownPercent}%", BearishRedLight)
                    }

                    Divider(color = ObsidianCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Executed Signals:", fontSize = 11.sp, color = TextMuted)
                        Text("${metrics.totalSignals} (${metrics.winCount} Wins / ${metrics.lossCount} Losses / ${metrics.breakevenCount} BE)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net Realized Pips:", fontSize = 11.sp, color = TextMuted)
                        Text("+${metrics.totalPipsRealized.toInt()} Pips", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BullishGreenLight)
                    }
                }
            }
        }

        // Transparency Standard Banner
        item {
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = CyanLight, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Transparency Policy: Every closed setup remains permanently logged. Losing trades and stop-outs are never purged or repainted.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Ledger History Header
        item {
            Text(
                text = "HISTORICAL TRADE RECORD (${completedSignals.size} TRADES)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        // Completed Trades List
        items(completedSignals, key = { it.id }) { trade ->
            JournalTradeRow(
                trade = trade,
                onClick = { viewModel.openChartForSignal(trade) }
            )
        }
    }
}

@Composable
fun JournalTradeRow(
    trade: SniperSignal,
    onClick: () -> Unit
) {
    val isWin = trade.state == SignalState.COMPLETED_TP1 || trade.state == SignalState.COMPLETED_TP2 || trade.state == SignalState.COMPLETED_TP3
    val isLoss = trade.state == SignalState.COMPLETED_SL
    val badgeColor = when {
        isWin -> BullishGreen
        isLoss -> BearishRed
        else -> TextMuted
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
        modifier = Modifier.fillMaxWidth().testTag("journal_row_${trade.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (trade.action == TradeAction.BUY_SNIPER) "BUY" else "SELL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(trade.instrumentSymbol, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text("• ${trade.timestamp}", fontSize = 10.sp, color = TextMuted)
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${trade.realizedRMultiple} (${trade.realizedGainPips.toInt()} pips)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Entry: ${trade.entryPrice} • SL: ${trade.stopLossPrice} • Exit: ${trade.exitDate}",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Text(
                text = "Reasoning: ${trade.manipulationNote}",
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color, modifier = Modifier.padding(top = 2.dp))
    }
}
