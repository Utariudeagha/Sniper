package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SniperSignal
import com.example.ui.theme.*

@Composable
fun RiskCalculatorDialog(
    signal: SniperSignal?,
    onDismiss: () -> Unit
) {
    var accountBalanceText by remember { mutableStateOf("10000") }
    var riskPercentText by remember { mutableStateOf("1.0") }
    var slPipsText by remember(signal) {
        mutableStateOf(signal?.riskPips?.toInt()?.toString() ?: "30")
    }

    val accountBalance = accountBalanceText.toDoubleOrNull() ?: 10000.0
    val riskPercent = riskPercentText.toDoubleOrNull() ?: 1.0
    val slPips = slPipsText.toDoubleOrNull() ?: 30.0

    // Risk Math
    val maxDollarRisk = accountBalance * (riskPercent / 100.0)
    val pipValuePerLot = 10.0 // Standard forex $10/pip per 1.00 lot
    val recommendedLotSize = if (slPips > 0) {
        val calculated = maxDollarRisk / (slPips * pipValuePerLot)
        "%.2f".format(calculated)
    } else "0.01"

    val tp1DollarReward = maxDollarRisk * 3.0 // 1:3 RR
    val tp2DollarReward = maxDollarRisk * 5.0 // 1:5 RR

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("risk_calculator_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Institutional Risk Calculator",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_risk_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                if (signal != null) {
                    Text(
                        text = "Pair: ${signal.instrumentSymbol} | SL: ${slPips.toInt()} Pips | Target: 1:3 & 1:5 RR",
                        fontSize = 11.sp,
                        color = CyanLight,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Inputs
                OutlinedTextField(
                    value = accountBalanceText,
                    onValueChange = { accountBalanceText = it },
                    label = { Text("Account Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_account_balance"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = riskPercentText,
                        onValueChange = { riskPercentText = it },
                        label = { Text("Risk % (1-2%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_risk_percent"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = slPipsText,
                        onValueChange = { slPipsText = it },
                        label = { Text("SL Pips") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_sl_pips"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calculated Results Card
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Dollar Risk:", fontSize = 12.sp, color = TextMuted)
                            Text(
                                "$${"%.2f".format(maxDollarRisk)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BearishRedLight
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = ObsidianCardBorder
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Recommended Lot Size:", fontSize = 13.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                            Text(
                                "$recommendedLotSize Lots",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = ObsidianCardBorder
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TP1 Reward (1:3 RR):", fontSize = 12.sp, color = TextMuted)
                            Text(
                                "+$${"%.2f".format(tp1DollarReward)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreenLight
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TP2 Reward (1:5 RR):", fontSize = 12.sp, color = TextMuted)
                            Text(
                                "+$${"%.2f".format(tp2DollarReward)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🛡️ 50-Year Market Law: Never exceed 1-2% risk per trade. Asymmetric 1:3 & 1:5 RR allows you to stay profitable even with a 40% win rate.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_risk_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Apply Risk Settings", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
