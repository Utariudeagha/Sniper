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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MarketViewModel

@Composable
fun SettingsScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val dataInfo by viewModel.dataProviderInfo.collectAsState()
    val equity by viewModel.accountEquity.collectAsState()
    val riskPct by viewModel.riskPercentPerTrade.collectAsState()
    val dailyLossPct by viewModel.dailyLossLimitPercent.collectAsState()
    val customKey by viewModel.customApiKey.collectAsState()
    val customProvider by viewModel.customApiProvider.collectAsState()
    val apiTestResult by viewModel.apiTestResult.collectAsState()

    var equityText by remember(equity) { mutableStateOf(equity.toInt().toString()) }
    var riskPctText by remember(riskPct) { mutableStateOf(riskPct.toString()) }
    var dailyLossText by remember(dailyLossPct) { mutableStateOf(dailyLossPct.toString()) }
    var keyInputText by remember(customKey) { mutableStateOf(customKey) }
    var selectedProvider by remember(customProvider) { mutableStateOf(customProvider) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Data Feed Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(dataInfo.status.badgeColorHex).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("settings_data_feed_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MARKET DATA PROVIDER & FEED STATUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(dataInfo.status.badgeColorHex))
                        )
                        Text(
                            text = dataInfo.status.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            InfoRow("Primary Source", dataInfo.primarySource)
                            InfoRow("Data Provider", dataInfo.providerName)
                            InfoRow("Feed Latency", "${dataInfo.feedLatencyMs} ms")
                            InfoRow("Timezone", dataInfo.timezone)
                            InfoRow("Access Model", "100% Free / Open Tier")
                        }
                    }
                }
            }
        }

        // Institutional Direct API Integration Card (Optional)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .testTag("settings_institutional_api_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIRECT INSTITUTIONAL API INTEGRATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = BullishGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "OPTIONAL KEY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreenLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The application already streams live real-time market data out-of-the-box from OpenER & Binance public APIs. If you hold a private broker API key, you can enter it below for direct tick routing.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("TWELVEDATA", "FINNHUB", "ALPHAVANTAGE").forEach { prov ->
                            val isSel = selectedProvider == prov
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedProvider = prov },
                                label = { Text(prov, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = ObsidianBackground
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = keyInputText,
                        onValueChange = { keyInputText = it },
                        label = { Text("$selectedProvider API Key") },
                        placeholder = { Text("Paste your API key here") },
                        modifier = Modifier.fillMaxWidth().testTag("settings_api_key_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.testExternalApi(selectedProvider, keyInputText.trim())
                            },
                            modifier = Modifier.weight(1f).testTag("test_api_connection_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test & Verify", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (keyInputText.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    keyInputText = ""
                                    viewModel.testExternalApi("DEFAULT_OPEN_INTERBANK", "")
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reset to Free", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }

                    if (apiTestResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val isSuccess = apiTestResult?.startsWith("✓") == true
                        Surface(
                            color = if (isSuccess) BullishGreen.copy(alpha = 0.15f) else BearishRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = apiTestResult.orEmpty(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSuccess) BullishGreenLight else BearishRedLight,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Risk Management Configuration Form
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
                        text = "ACCOUNT EQUITY & RISK DISCIPLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = equityText,
                        onValueChange = { equityText = it },
                        label = { Text("Account Balance ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("settings_equity_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = riskPctText,
                            onValueChange = { riskPctText = it },
                            label = { Text("Risk % (Default 0.5%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("settings_risk_pct_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = dailyLossText,
                            onValueChange = { dailyLossText = it },
                            label = { Text("Max Daily Loss %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("settings_daily_loss_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val eq = equityText.toDoubleOrNull() ?: 10000.0
                            val r = riskPctText.toDoubleOrNull() ?: 0.5
                            val dl = dailyLossText.toDoubleOrNull() ?: 2.0
                            viewModel.updateRiskSettings(eq, r, dl)
                            showSavedSnackbar = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_settings_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Institutional Risk Profile", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }

                    if (showSavedSnackbar) {
                        Text(
                            text = "✓ Risk configuration updated successfully",
                            fontSize = 11.sp,
                            color = BullishGreenLight,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        // Strictly Free Access & Compliance Disclosures
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
                        text = "STRICTLY FREE ARCHITECTURE & COMPLIANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• No Mandatory Paid Plans: All features, multi-timeframe analysis, and TradingView charts are genuinely free.\n" +
                                "• Local Deterministic Engine: Technical analysis is executed algorithmically on closed candles without relying on costly external AI APIs.\n" +
                                "• Zero Unrealistic Claims: We never claim a 99% win rate. When conditions fail or data is choppy, the system strictly outputs NO TRADE to protect capital.\n" +
                                "• TradingView Permitted Embedding: Standard free widget embedding with full technical charting tools.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
    }
}
