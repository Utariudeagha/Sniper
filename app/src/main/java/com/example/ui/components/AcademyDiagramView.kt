package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiagramType
import com.example.ui.theme.*

@Composable
fun AcademyDiagramView(
    diagramType: DiagramType,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        val title = when (diagramType) {
            DiagramType.LIQUIDITY_SWEEP_DIAGRAM -> "VISUAL: Liquidity Sweep & Stop Hunt Anatomy"
            DiagramType.JUDAS_SWING_DIAGRAM -> "VISUAL: The London Judas Swing Cycle"
            DiagramType.ORDER_BLOCK_DIAGRAM -> "VISUAL: Order Block Footprint"
            DiagramType.FAIR_VALUE_GAP_DIAGRAM -> "VISUAL: Fair Value Gap (FVG) Imbalance"
            DiagramType.TOP_DOWN_PYRAMID -> "VISUAL: 8-Timeframe Confluence Hierarchy"
            DiagramType.RISK_REWARD_LADDER -> "VISUAL: Asymmetric 1:3 to 1:5 Math Ladder"
        }

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GoldLight,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val w = size.width
            val h = size.height

            when (diagramType) {
                DiagramType.JUDAS_SWING_DIAGRAM -> {
                    // Draw Asian Session Box
                    val asianBoxWidth = w * 0.32f
                    val asianBoxHeight = h * 0.45f
                    val asianTop = h * 0.32f

                    drawRect(
                        color = ObsidianSubtle,
                        topLeft = Offset(w * 0.05f, asianTop),
                        size = Size(asianBoxWidth, asianBoxHeight)
                    )
                    drawRect(
                        color = TextMuted.copy(alpha = 0.5f),
                        topLeft = Offset(w * 0.05f, asianTop),
                        size = Size(asianBoxWidth, asianBoxHeight),
                        style = Stroke(1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "ASIAN CONSOLIDATION",
                        topLeft = Offset(w * 0.07f, asianTop + 8.dp.toPx()),
                        style = TextStyle(color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )

                    // Draw Judas Swing Trap (Downwards fakeout)
                    val judasStartX = w * 0.37f
                    val judasBottom = h * 0.90f
                    drawLine(
                        color = BearishRed,
                        start = Offset(judasStartX, asianTop + asianBoxHeight * 0.6f),
                        end = Offset(w * 0.48f, judasBottom),
                        strokeWidth = 3f
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "JUDAS SWING (TRAP)",
                        topLeft = Offset(w * 0.38f, judasBottom - 18.dp.toPx()),
                        style = TextStyle(color = BearishRedLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )

                    // Draw Smart Money Buy Reversal & Expansion
                    drawLine(
                        color = BullishGreen,
                        start = Offset(w * 0.48f, judasBottom),
                        end = Offset(w * 0.92f, h * 0.15f),
                        strokeWidth = 4f
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "INSTITUTIONAL EXPANSION (+1:5 RR)",
                        topLeft = Offset(w * 0.55f, h * 0.18f),
                        style = TextStyle(color = BullishGreenLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )
                }

                DiagramType.ORDER_BLOCK_DIAGRAM, DiagramType.FAIR_VALUE_GAP_DIAGRAM -> {
                    // Candle 1 (Bearish)
                    val c1X = w * 0.18f
                    drawLine(color = BearishRed, start = Offset(c1X, h * 0.4f), end = Offset(c1X, h * 0.8f), strokeWidth = 1.5f)
                    drawRect(color = BearishRed, topLeft = Offset(c1X - 10.dp.toPx(), h * 0.45f), size = Size(20.dp.toPx(), h * 0.25f))

                    // Candle 2 (Giant Bullish Displacement)
                    val c2X = w * 0.38f
                    drawLine(color = BullishGreen, start = Offset(c2X, h * 0.15f), end = Offset(c2X, h * 0.75f), strokeWidth = 1.5f)
                    drawRect(color = BullishGreen, topLeft = Offset(c2X - 12.dp.toPx(), h * 0.2f), size = Size(24.dp.toPx(), h * 0.5f))

                    // Candle 3
                    val c3X = w * 0.58f
                    drawLine(color = BullishGreen, start = Offset(c3X, h * 0.1f), end = Offset(c3X, h * 0.35f), strokeWidth = 1.5f)
                    drawRect(color = BullishGreen, topLeft = Offset(c3X - 10.dp.toPx(), h * 0.12f), size = Size(20.dp.toPx(), h * 0.2f))

                    // FVG Zone between C1 High and C3 Low
                    val fvgTop = h * 0.35f
                    val fvgBottom = h * 0.45f
                    drawRect(
                        color = CyanAccent.copy(alpha = 0.25f),
                        topLeft = Offset(w * 0.25f, fvgTop),
                        size = Size(w * 0.65f, fvgBottom - fvgTop)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "FAIR VALUE GAP (IMBALANCE)",
                        topLeft = Offset(w * 0.35f, fvgTop + 2.dp.toPx()),
                        style = TextStyle(color = CyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )

                    // Retest Arrow
                    drawLine(
                        color = GoldLight,
                        start = Offset(w * 0.75f, h * 0.2f),
                        end = Offset(w * 0.82f, fvgBottom - 2.dp.toPx()),
                        strokeWidth = 2.5f
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "SNIPER ENTRY TAP",
                        topLeft = Offset(w * 0.62f, h * 0.55f),
                        style = TextStyle(color = GoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )
                }

                DiagramType.TOP_DOWN_PYRAMID -> {
                    // Draw 4 tiered blocks
                    val layers = listOf(
                        "1. MONTHLY & WEEKLY: Macro Order Flow Bias",
                        "2. DAILY & 4-HOUR: Key Supply/Demand POIs",
                        "3. 1-HOUR & 30-MIN: Liquidity Traps & Sweeps",
                        "4. 15-MIN & 5-MIN: Sniper CHoCH Trigger & Entry"
                    )

                    layers.forEachIndexed { i, label ->
                        val y = h * (0.12f + i * 0.22f)
                        val boxW = w * (0.92f - i * 0.08f)
                        val x = (w - boxW) / 2f

                        val color = if (i == 3) GoldPrimary.copy(alpha = 0.25f) else ObsidianSubtle
                        val border = if (i == 3) GoldLight else CyanDark

                        drawRect(color = color, topLeft = Offset(x, y), size = Size(boxW, 26.dp.toPx()))
                        drawRect(color = border, topLeft = Offset(x, y), size = Size(boxW, 26.dp.toPx()), style = Stroke(1.5f))
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = Offset(x + 10.dp.toPx(), y + 6.dp.toPx()),
                            style = TextStyle(color = if (i == 3) GoldLight else TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                else -> {
                    // Risk-Reward Ladder
                    val slY = h * 0.85f
                    val entryY = h * 0.70f
                    val tp1Y = h * 0.40f
                    val tp2Y = h * 0.15f

                    // SL Line
                    drawLine(color = BearishRed, start = Offset(w * 0.1f, slY), end = Offset(w * 0.9f, slY), strokeWidth = 2f)
                    drawText(textMeasurer = textMeasurer, text = "STOP LOSS (-1R RISK)", topLeft = Offset(w * 0.12f, slY - 14.dp.toPx()), style = TextStyle(color = BearishRed, fontSize = 10.sp, fontWeight = FontWeight.Bold))

                    // Entry Line
                    drawLine(color = CyanAccent, start = Offset(w * 0.1f, entryY), end = Offset(w * 0.9f, entryY), strokeWidth = 2f)
                    drawText(textMeasurer = textMeasurer, text = "SNIPER ENTRY (0R)", topLeft = Offset(w * 0.12f, entryY - 14.dp.toPx()), style = TextStyle(color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold))

                    // TP1 Line
                    drawLine(color = BullishGreen, start = Offset(w * 0.1f, tp1Y), end = Offset(w * 0.9f, tp1Y), strokeWidth = 2f)
                    drawText(textMeasurer = textMeasurer, text = "TAKE PROFIT 1 (+3R REWARD)", topLeft = Offset(w * 0.12f, tp1Y - 14.dp.toPx()), style = TextStyle(color = BullishGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold))

                    // TP2 Line
                    drawLine(color = GoldLight, start = Offset(w * 0.1f, tp2Y), end = Offset(w * 0.9f, tp2Y), strokeWidth = 2.5f)
                    drawText(textMeasurer = textMeasurer, text = "TAKE PROFIT 2 (+5R RUNNER)", topLeft = Offset(w * 0.12f, tp2Y - 14.dp.toPx()), style = TextStyle(color = GoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
