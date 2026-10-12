package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MarketViewModel

@Composable
fun RiskDisciplineScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    var equityText by remember { mutableStateOf("10000") }
    var riskPercentText by remember { mutableStateOf("1.0") }
    var stopLossPipsText by remember { mutableStateOf("25") }

    val equity = equityText.toDoubleOrNull() ?: 10000.0
    val riskPercent = riskPercentText.toDoubleOrNull() ?: 1.0
    val stopLossPips = stopLossPipsText.toDoubleOrNull() ?: 25.0

    val maxDollarRisk = equity * (riskPercent / 100.0)
    val pipValue = 10.0 // Standard $10/pip on 1 lot
    val recommendedLot = if (stopLossPips > 0) {
        val calculated = maxDollarRisk / (stopLossPips * pipValue)
        "%.2f".format(calculated)
    } else "0.01"

    val tp1DollarReward = maxDollarRisk * 3.0 // 1:3 RR
    val tp2DollarReward = maxDollarRisk * 5.0 // 1:5 RR

    // Discipline Checklist States
    var checkedRule1 by remember { mutableStateOf(true) }
    var checkedRule2 by remember { mutableStateOf(true) }
    var checkedRule3 by remember { mutableStateOf(false) }
    var checkedRule4 by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cockpit Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("risk_cockpit_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        color = GoldPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Text(
                            text = "CAPITAL PRESERVATION & DISCIPLINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "50-Year Institutional Risk Engine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )

                    Text(
                        text = "Risk strictly 1% to 2% per trade. Demand 1:3 to 1:5 Risk to Reward. Mathematically impossible to blow your account when position sizing is respected.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Live Risk & Lot Calculator Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SNIPER POSITION SIZING CALCULATOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = equityText,
                        onValueChange = { equityText = it },
                        label = { Text("Account Balance / Equity ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("equity_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = riskPercentText,
                            onValueChange = { riskPercentText = it },
                            label = { Text("Risk % (Max 2%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("risk_pct_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = stopLossPipsText,
                            onValueChange = { stopLossPipsText = it },
                            label = { Text("Stop Loss (Pips)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sl_pips_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Result Matrix
                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("RECOMMENDED POSITION SIZE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(
                                        "$recommendedLot Standard Lots",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldLight
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("MAX DOLLAR RISK", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(
                                        "-$${"%.2f".format(maxDollarRisk)}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BearishRedLight
                                    )
                                }
                            }

                            Divider(color = ObsidianCardBorder, modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("TARGET 1 (1:3 RR)", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(
                                        "+$${"%.2f".format(tp1DollarReward)} (+${(riskPercent * 3.0)}%)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BullishGreenLight
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("TARGET 2 (1:5 RR RUNNER)", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(
                                        "+$${"%.2f".format(tp2DollarReward)} (+${(riskPercent * 5.0)}%)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Asymmetric Risk Mathematics Table
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "THE 1:3 & 1:5 ASYMMETRIC ADVANTAGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "See how asymmetric R:R protects you across 10 sample trades:",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Scenario Rows
                    ExpectancyRow("40% Win Rate at 1:3 RR", "4 Wins (+12R) - 6 Losses (-6R)", "+6.0 R Net Profit (+6% to +12%)", BullishGreen)
                    ExpectancyRow("50% Win Rate at 1:3 RR", "5 Wins (+15R) - 5 Losses (-5R)", "+10.0 R Net Profit (+10% to +20%)", BullishGreen)
                    ExpectancyRow("40% Win Rate at 1:5 RR", "4 Wins (+20R) - 6 Losses (-6R)", "+14.0 R Net Profit (+14% to +28%)", GoldLight)
                    ExpectancyRow("30% Win Rate at 1:5 RR", "3 Wins (+15R) - 7 Losses (-7R)", "+8.0 R Net Profit (Still highly profitable!)", CyanLight)
                }
            }
        }

        // Pre-Execution Psychological Discipline Checklist
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = CyanLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "PRE-TRADE DISCIPLINE GATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DisciplineCheckRow("HTF alignment confirmed (Monthly/Weekly/Daily agree with trade)", checkedRule1) { checkedRule1 = it }
                    DisciplineCheckRow("Liquidity sweep occurred (Asian or session highs/lows purged)", checkedRule2) { checkedRule2 = it }
                    DisciplineCheckRow("Position size strictly set to 1-2% max risk (no greed sizing)", checkedRule3) { checkedRule3 = it }
                    DisciplineCheckRow("No high-impact red folder news in next 2 hours", checkedRule4) { checkedRule4 = it }
                }
            }
        }
    }
}

@Composable
fun ExpectancyRow(title: String, formula: String, outcome: String, outcomeColor: Color) {
    Surface(
        color = ObsidianSurface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(formula, fontSize = 11.sp, color = TextMuted)
            Text(outcome, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = outcomeColor, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun DisciplineCheckRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (checked) TextWhite else TextMuted,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
