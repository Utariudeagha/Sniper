package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SignalState
import com.example.model.SniperSignal
import com.example.model.TradeAction
import com.example.ui.theme.*

@Composable
fun SniperSignalCard(
    signal: SniperSignal,
    isWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onOpenChart: () -> Unit,
    onCalculateRisk: () -> Unit,
    modifier: Modifier = Modifier,
    onAcknowledge: (() -> Unit)? = null
) {
    val isBuy = signal.action == TradeAction.BUY_SNIPER
    val accentColor = if (isBuy) BullishGreen else BearishRed
    val isFailed = signal.state == SignalState.FAILED || signal.state == SignalState.COMPLETED_SL
    val isPotential = signal.state == SignalState.POTENTIAL_DEVELOPING
    val isCompleted = signal.state == SignalState.COMPLETED_TP1 ||
            signal.state == SignalState.COMPLETED_TP2 ||
            signal.state == SignalState.COMPLETED_SL ||
            signal.state == SignalState.FAILED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (signal.confluenceScore >= 90) GoldPrimary.copy(alpha = 0.6f) else ObsidianCardBorder, RoundedCornerShape(16.dp))
            .clickable { onOpenChart() }
            .testTag("signal_card_${signal.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Symbol, Action badge, Confluence pill & Watchlist star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Action Pill (BUY SNIPER / SELL SNIPER)
                    Surface(
                        color = accentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isBuy) "BUY SNIPER" else "SELL SNIPER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }

                    Column {
                        Text(
                            text = signal.instrumentSymbol,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = signal.instrumentName,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Confluence Score Pill
                    Surface(
                        color = if (signal.confluenceScore >= 88) GoldPrimary.copy(alpha = 0.2f) else CyanDark.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${signal.confluenceScore}% Match",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (signal.confluenceScore >= 88) GoldLight else CyanLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleWatchlist,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("watchlist_btn_${signal.instrumentSymbol}")
                    ) {
                        Icon(
                            imageVector = if (isWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Watchlist",
                            tint = if (isWatchlist) GoldLight else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing Matrix: Entry | Stop Loss | TP1 (1:3) | TP2 (1:5)
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Entry Price
                    Column {
                        Text("SNIPER ENTRY", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Text(
                            text = "%.4f".format(signal.entryPrice).trimEnd('0').trimEnd('.'),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight
                        )
                    }

                    // Stop Loss
                    Column {
                        Text("STOP LOSS", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Text(
                            text = "%.4f".format(signal.stopLossPrice).trimEnd('0').trimEnd('.'),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BearishRed
                        )
                        Text("${signal.riskPips.toInt()} pips", fontSize = 9.sp, color = BearishRedLight)
                    }

                    // TP1 (1:3)
                    Column {
                        Text("TP1 (1:3 RR)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Text(
                            text = "%.4f".format(signal.takeProfit1Price).trimEnd('0').trimEnd('.'),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen
                        )
                        Text("+${signal.tp1Pips.toInt()} pips", fontSize = 9.sp, color = BullishGreenLight)
                    }

                    // TP2 (1:5)
                    Column {
                        Text("TP2 (1:5 RR)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Text(
                            text = "%.4f".format(signal.takeProfit2Price).trimEnd('0').trimEnd('.'),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Text("+${signal.tp2Pips.toInt()} pips", fontSize = 9.sp, color = GoldLight)
                    }
                }
            }

            // Real-Time Live Market Execution & Floating PnL
            if (signal.liveCurrentPrice > 0.0 && !isFailed && signal.state != SignalState.NO_SETUP_PROTECTED) {
                Spacer(modifier = Modifier.height(8.dp))
                val isProfit = signal.floatingPips >= 0.0
                val pnlColor = if (isProfit) BullishGreen else BearishRed
                Surface(
                    color = pnlColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, pnlColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(pnlColor))
                            Text(
                                text = "LIVE MARKET: %.4f".format(signal.liveCurrentPrice).trimEnd('0').trimEnd('.'),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        Text(
                            text = "${if (isProfit) "+" else ""}${signal.floatingPips} pips (${if (signal.floatingPnLUsd >= 0) "+$" else "-$"}${kotlin.math.abs(signal.floatingPnLUsd)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = pnlColor
                        )
                    }
                }
            }

            // Institutional & Market Maker Insight
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = signal.manipulationNote,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }

            // Failed Setup Post-Mortem Audit Trail
            if (isFailed) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = BearishRed.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BearishRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = BearishRedLight, modifier = Modifier.size(16.dp))
                                Text("FAILURE POST-MORTEM EVIDENCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BearishRedLight)
                            }
                            Surface(
                                color = if (signal.isAcknowledged) ObsidianSubtle else BearishRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (signal.isAcknowledged) "ACKNOWLEDGED" else "UNVIEWED FAILURE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (signal.isAcknowledged) TextMuted else TextWhite,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (signal.failureReason.isNotBlank()) {
                            Text(
                                text = signal.failureReason,
                                fontSize = 11.sp,
                                color = TextWhite,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Observed Exit: ${if (signal.failurePrice > 0.0) signal.failurePrice else signal.stopLossPrice}",
                                fontSize = 10.sp,
                                color = BearishRedLight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = signal.failureTimestamp.ifEmpty { signal.exitDate },
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        if (!signal.isAcknowledged && onAcknowledge != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = onAcknowledge,
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BearishRed.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .testTag("ack_failure_${signal.id}")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BullishGreenLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Acknowledge & Archive Alert", fontSize = 10.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Potential Setup Criteria Matrix
            if (isPotential) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = CyanDark.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanDark.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = CyanLight, modifier = Modifier.size(15.dp))
                                Text("SETUP ACTIVATION CHECKLIST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanLight)
                            }
                            if (signal.setupAge.isNotBlank()) {
                                Text(signal.setupAge, fontSize = 10.sp, color = TextMuted)
                            }
                        }

                        if (signal.conditionsMet.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Satisfied Conditions:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BullishGreenLight)
                            signal.conditionsMet.forEach { cond ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(vertical = 1.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BullishGreen, modifier = Modifier.size(13.dp))
                                    Text(cond, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
                                }
                            }
                        }

                        if (signal.remainingConditions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Remaining Activation Triggers:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                            signal.remainingConditions.forEach { cond ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(vertical = 1.dp)
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldLight, modifier = Modifier.size(13.dp))
                                    Text(cond, fontSize = 10.sp, color = TextWhite, lineHeight = 14.sp)
                                }
                            }
                        }

                        if (signal.expiryConditions.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Expiry Rule: ${signal.expiryConditions}",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // Completed Outcome Banner (if completed winning trade)
            if (isCompleted && !isFailed) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = BullishGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VERIFIED OUTCOME: ${signal.realizedRMultiple} (${signal.realizedGainPips.toInt()} Pips)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreenLight
                        )
                        Text(
                            text = signal.exitDate,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Open Interactive Chart & Risk Calculator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCalculateRisk,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calc_risk_${signal.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Risk & Lot Calc", fontSize = 12.sp)
                }

                Button(
                    onClick = onOpenChart,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chart_view_${signal.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TradingView Chart", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
