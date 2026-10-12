package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Timeframe
import com.example.model.TimeframeConfluence
import com.example.model.TrendDirection
import com.example.ui.theme.*

@Composable
fun TopdownMatrixView(
    confluences: List<TimeframeConfluence>,
    confluenceScore: Int,
    selectedTimeframe: Timeframe,
    onSelectTimeframe: (Timeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHighProbability = confluenceScore >= 88

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianCard, RoundedCornerShape(14.dp))
            .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("topdown_matrix_card")
    ) {
        // Confluence Header & Score Gauge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOP-DOWN CONFLUENCE MATRIX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isHighProbability) "Institutional A+ Alignment" else "Low Probability / Mixed Bias",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isHighProbability) BullishGreenLight else BearishRedLight
                )
            }

            // Score Pill
            Surface(
                color = if (isHighProbability) BullishGreen.copy(alpha = 0.2f) else BearishRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isHighProbability) BullishGreen else BearishRed
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isHighProbability) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isHighProbability) BullishGreen else BearishRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$confluenceScore% Confluence",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar representing confluence
        LinearProgressIndicator(
            progress = { (confluenceScore / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isHighProbability) BullishGreen else BearishRed,
            trackColor = ObsidianSubtle,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Core Rule Callout
        Surface(
            color = ObsidianSubtle.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚡ Golden Rule: \"Alignment of LTF price delivery with HTF bias is the highest level of mastery there is.\"",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Timeframe Selector & Confluence Breakdown
        Text(
            text = "Timeframe Institutional Alignment (Tap to View Chart):",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 8 Timeframes grid / rows
        confluences.forEach { item ->
            val isSelected = item.timeframe == selectedTimeframe
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) CyanDark.copy(alpha = 0.25f) else ObsidianSurface,
                label = "tf_bg"
            )

            Surface(
                onClick = { onSelectTimeframe(item.timeframe) },
                shape = RoundedCornerShape(8.dp),
                color = backgroundColor,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) CyanAccent else ObsidianCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .testTag("tf_row_${item.timeframe.code}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Timeframe code + Role badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (item.timeframe.isHTF) GoldDark.copy(alpha = 0.3f) else CyanDark.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.timeframe.code,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.timeframe.isHTF) GoldLight else CyanLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Column {
                            Text(
                                text = item.timeframe.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Text(
                                text = item.marketStructure,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Right: Trend Direction & Alignment POI
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Trend Pill
                        val trendColor = when (item.trend) {
                            TrendDirection.BULLISH -> BullishGreen
                            TrendDirection.BEARISH -> BearishRed
                            TrendDirection.RANGING -> TextMuted
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(trendColor)
                            )
                            Text(
                                text = item.trend.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = trendColor
                            )
                        }

                        // Aligned check
                        Icon(
                            imageVector = if (item.isAlignedWithHTF) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (item.isAlignedWithHTF) BullishGreen else BearishRed,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
