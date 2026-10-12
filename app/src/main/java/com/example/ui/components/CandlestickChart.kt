package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import com.example.model.Candlestick
import com.example.model.MarketInstrument
import com.example.model.SmartMoneyMarker
import com.example.model.SniperSignal
import com.example.ui.theme.*

@Composable
fun CandlestickChart(
    instrument: MarketInstrument,
    candles: List<Candlestick>,
    activeSignal: SniperSignal?,
    smcMarkers: List<SmartMoneyMarker>,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(ObsidianCard, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading institutional market data...", color = TextMuted)
        }
        return
    }

    var selectedCandleIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()

    val minPrice = remember(candles, activeSignal) {
        val candleMin = candles.minOf { it.low }
        val signalMin = activeSignal?.let { minOf(it.stopLossPrice, it.entryPrice) } ?: candleMin
        minOf(candleMin, signalMin) * 0.9985
    }

    val maxPrice = remember(candles, activeSignal) {
        val candleMax = candles.maxOf { it.high }
        val signalMax = activeSignal?.let { maxOf(it.takeProfit2Price, it.entryPrice) } ?: candleMax
        maxOf(candleMax, signalMax) * 1.0015
    }

    val priceRange = (maxPrice - minPrice).coerceAtLeast(0.0001)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("candlestick_chart_container")
    ) {
        // Inspection HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val inspectedCandle = selectedCandleIndex?.let { candles.getOrNull(it) } ?: candles.lastOrNull()
            if (inspectedCandle != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "O: ${instrument.formatPrice(inspectedCandle.open)}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "H: ${instrument.formatPrice(inspectedCandle.high)}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "L: ${instrument.formatPrice(inspectedCandle.low)}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "C: ${instrument.formatPrice(inspectedCandle.close)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (inspectedCandle.isBullish) BullishGreen else BearishRed
                    )
                }
            }

            Text(
                text = "Tap & scrub chart to inspect",
                fontSize = 10.sp,
                color = CyanAccent
            )
        }

        // Canvas Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("candlestick_canvas")
                    .pointerInput(candles) {
                        detectTapGestures(
                            onTap = { offset ->
                                val candleWidth = size.width / candles.size
                                val index = (offset.x / candleWidth).toInt().coerceIn(0, candles.lastIndex)
                                selectedCandleIndex = index
                            }
                        )
                    }
                    .pointerInput(candles) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val candleWidth = size.width / candles.size
                                val index = (offset.x / candleWidth).toInt().coerceIn(0, candles.lastIndex)
                                selectedCandleIndex = index
                            },
                            onDrag = { change, _ ->
                                val candleWidth = size.width / candles.size
                                val index = (change.position.x / candleWidth).toInt().coerceIn(0, candles.lastIndex)
                                selectedCandleIndex = index
                            },
                            onDragEnd = {
                                selectedCandleIndex = null
                            }
                        )
                    }
            ) {
                val chartWidth = size.width - 65.dp.toPx() // leave space for right price axis
                val chartHeight = size.height
                val candleCount = candles.size
                val candleSlotWidth = chartWidth / candleCount
                val candleBodyWidth = (candleSlotWidth * 0.65f).coerceAtLeast(3f)

                fun priceToY(price: Double): Float {
                    val normalized = ((maxPrice - price) / priceRange).toFloat()
                    return normalized * chartHeight
                }

                // 1. Draw horizontal institutional price grid lines
                val gridSteps = 5
                for (i in 0..gridSteps) {
                    val priceLevel = minPrice + (priceRange * (i.toDouble() / gridSteps))
                    val y = priceToY(priceLevel)
                    drawLine(
                        color = ObsidianCardBorder.copy(alpha = 0.6f),
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1f
                    )

                    // Draw right-axis price label
                    val priceLabel = instrument.formatPrice(priceLevel)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = priceLabel,
                        topLeft = Offset(chartWidth + 6.dp.toPx(), y - 7.sp.toPx()),
                        style = TextStyle(color = TextMuted, fontSize = 9.sp)
                    )
                }

                // 2. Draw Smart Money Overlays (Order Blocks & FVGs)
                smcMarkers.forEach { marker ->
                    val topY = priceToY(maxOf(marker.startPrice, marker.endPrice))
                    val bottomY = priceToY(minOf(marker.startPrice, marker.endPrice))
                    val boxHeight = (bottomY - topY).coerceAtLeast(4f)

                    val (fillColor, borderColor) = when (marker.type) {
                        com.example.model.MarkerType.ORDER_BLOCK_BULLISH ->
                            Pair(BullishGreen.copy(alpha = 0.15f), BullishGreen.copy(alpha = 0.5f))
                        com.example.model.MarkerType.ORDER_BLOCK_BEARISH ->
                            Pair(BearishRed.copy(alpha = 0.15f), BearishRed.copy(alpha = 0.5f))
                        com.example.model.MarkerType.FAIR_VALUE_GAP ->
                            Pair(CyanAccent.copy(alpha = 0.18f), CyanAccent.copy(alpha = 0.6f))
                        else ->
                            Pair(GoldPrimary.copy(alpha = 0.12f), GoldPrimary.copy(alpha = 0.4f))
                    }

                    // Render shaded zone across right half of chart
                    val zoneStartX = chartWidth * 0.35f
                    val zoneWidth = chartWidth * 0.65f
                    drawRect(
                        color = fillColor,
                        topLeft = Offset(zoneStartX, topY),
                        size = Size(zoneWidth, boxHeight)
                    )
                    drawRect(
                        color = borderColor,
                        topLeft = Offset(zoneStartX, topY),
                        size = Size(zoneWidth, boxHeight),
                        style = Stroke(width = 1f)
                    )

                    // Overlay label
                    drawText(
                        textMeasurer = textMeasurer,
                        text = marker.label,
                        topLeft = Offset(zoneStartX + 4.dp.toPx(), topY + 2.dp.toPx()),
                        style = TextStyle(color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    )
                }

                // 3. Draw Candlesticks
                candles.forEachIndexed { index, candle ->
                    val centerX = (index * candleSlotWidth) + (candleSlotWidth / 2f)
                    val openY = priceToY(candle.open)
                    val closeY = priceToY(candle.close)
                    val highY = priceToY(candle.high)
                    val lowY = priceToY(candle.low)

                    val candleColor = if (candle.isBullish) BullishGreen else BearishRed

                    // Draw Wick
                    drawLine(
                        color = candleColor,
                        start = Offset(centerX, highY),
                        end = Offset(centerX, lowY),
                        strokeWidth = 1.5f
                    )

                    // Draw Body
                    val bodyTop = minOf(openY, closeY)
                    val bodyHeight = (abs(openY - closeY)).coerceAtLeast(2f)
                    val bodyLeft = centerX - (candleBodyWidth / 2f)

                    drawRect(
                        color = candleColor,
                        topLeft = Offset(bodyLeft, bodyTop),
                        size = Size(candleBodyWidth, bodyHeight)
                    )
                }

                // 4. Draw Active Sniper Signal Target Overlays (Entry, SL, TP1 1:3, TP2 1:5)
                activeSignal?.let { signal ->
                    val entryY = priceToY(signal.entryPrice)
                    val slY = priceToY(signal.stopLossPrice)
                    val tp1Y = priceToY(signal.takeProfit1Price)
                    val tp2Y = priceToY(signal.takeProfit2Price)

                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)

                    // Stop Loss (Red)
                    drawLine(
                        color = BearishRed,
                        start = Offset(0f, slY),
                        end = Offset(chartWidth, slY),
                        strokeWidth = 2f,
                        pathEffect = dashEffect
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "SL: ${instrument.formatPrice(signal.stopLossPrice)}",
                        topLeft = Offset(chartWidth + 6.dp.toPx(), slY - 7.sp.toPx()),
                        style = TextStyle(color = BearishRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )

                    // Sniper Entry (Cyan / Gold)
                    drawLine(
                        color = CyanAccent,
                        start = Offset(0f, entryY),
                        end = Offset(chartWidth, entryY),
                        strokeWidth = 2.5f
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "ENTRY: ${instrument.formatPrice(signal.entryPrice)}",
                        topLeft = Offset(chartWidth + 6.dp.toPx(), entryY - 7.sp.toPx()),
                        style = TextStyle(color = CyanAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )

                    // TP1 (1:3 RR)
                    drawLine(
                        color = BullishGreen,
                        start = Offset(0f, tp1Y),
                        end = Offset(chartWidth, tp1Y),
                        strokeWidth = 2f,
                        pathEffect = dashEffect
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "TP1 (1:3): ${instrument.formatPrice(signal.takeProfit1Price)}",
                        topLeft = Offset(chartWidth + 6.dp.toPx(), tp1Y - 7.sp.toPx()),
                        style = TextStyle(color = BullishGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )

                    // TP2 (1:5 RR)
                    drawLine(
                        color = GoldLight,
                        start = Offset(0f, tp2Y),
                        end = Offset(chartWidth, tp2Y),
                        strokeWidth = 2f,
                        pathEffect = dashEffect
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "TP2 (1:5): ${instrument.formatPrice(signal.takeProfit2Price)}",
                        topLeft = Offset(chartWidth + 6.dp.toPx(), tp2Y - 7.sp.toPx()),
                        style = TextStyle(color = GoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )
                }

                // 5. Draw scrub crosshair if active
                selectedCandleIndex?.let { index ->
                    if (index in candles.indices) {
                        val inspectedCandle = candles[index]
                        val x = (index * candleSlotWidth) + (candleSlotWidth / 2f)
                        val y = priceToY(inspectedCandle.close)

                        // Vertical guideline
                        drawLine(
                            color = TextWhite.copy(alpha = 0.5f),
                            start = Offset(x, 0f),
                            end = Offset(x, chartHeight),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                        )

                        // Price crosshair point
                        drawCircle(
                            color = GoldLight,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }
    }
}
