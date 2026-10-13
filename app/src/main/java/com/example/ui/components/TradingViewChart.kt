package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.MarketInstrument
import com.example.model.SignalState
import com.example.model.SniperSignal
import com.example.model.Timeframe
import com.example.model.TradeAction
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TradingViewChart(
    instrument: MarketInstrument,
    timeframe: Timeframe,
    signal: SniperSignal? = null,
    allSignals: List<SniperSignal> = emptyList(),
    onSelectSignal: ((SniperSignal) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showLevelsOverlay by remember { mutableStateOf(true) }
    var showSignalsCarousel by remember { mutableStateOf(true) }
    var isExpanded by remember { mutableStateOf(false) }

    val fastWidgetHtml = remember(instrument.tradingViewSymbol, timeframe.tvInterval) {
        generateFastTradingViewEmbed(instrument.tradingViewSymbol, timeframe.tvInterval)
    }

    val isBuy = signal?.action == TradeAction.BUY_SNIPER
    val isSell = signal?.action == TradeAction.SELL_SNIPER
    val isNoTrade = signal?.action == TradeAction.NO_TRADE
    val isPotential = signal?.state == SignalState.POTENTIAL_DEVELOPING
    val isFailed = signal?.state == SignalState.FAILED || signal?.state == SignalState.COMPLETED_SL

    val primarySignalColor = when {
        isNoTrade -> GoldPrimary
        isPotential -> CyanLight
        isBuy -> BullishGreen
        isSell -> BearishRed
        isFailed -> BearishRedLight
        else -> TextMuted
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianCard, RoundedCornerShape(14.dp))
            .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            .padding(10.dp)
            .testTag("tradingview_chart_container")
    ) {
        // TradingView Top Header Bar with Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = CyanDark.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = CyanLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "TradingView Live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight
                        )
                    }
                }

                Text(
                    text = "${instrument.tradingViewSymbol} • ${timeframe.code}",
                    fontSize = 11.sp,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )

                if (signal != null) {
                    Surface(
                        color = primarySignalColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = when {
                                isBuy -> "BUY ${signal.riskRewardRatio}"
                                isSell -> "SELL ${signal.riskRewardRatio}"
                                isPotential -> "DEVELOPING"
                                isFailed -> "FAILED SL"
                                else -> "NO TRADE"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = primarySignalColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            // Quick Chart Action Icons
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                // Toggle Signals Bar
                IconButton(
                    onClick = { showSignalsCarousel = !showSignalsCarousel },
                    modifier = Modifier.size(28.dp).testTag("toggle_signals_carousel")
                ) {
                    Icon(
                        imageVector = if (showSignalsCarousel) Icons.Default.ViewList else Icons.Default.FormatListBulleted,
                        contentDescription = "Toggle Signals Bar",
                        tint = if (showSignalsCarousel) GoldLight else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Toggle On-Chart Price Levels
                IconButton(
                    onClick = { showLevelsOverlay = !showLevelsOverlay },
                    modifier = Modifier.size(28.dp).testTag("toggle_levels_overlay")
                ) {
                    Icon(
                        imageVector = if (showLevelsOverlay) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Levels Overlay",
                        tint = if (showLevelsOverlay) CyanLight else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Expand / Collapse Chart Height
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp).testTag("toggle_expand_chart")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Expand Chart View",
                        tint = if (isExpanded) GoldLight else TextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Reload Chart
                IconButton(
                    onClick = { webViewInstance?.reload() },
                    modifier = Modifier.size(28.dp).testTag("reload_tv_chart")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload TradingView Chart",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Signals on TradingView Carousel / Quick Selector
        AnimatedVisibility(
            visible = showSignalsCarousel && allSignals.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIGNALS ON TRADINGVIEW LIVE CHART",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Tap to render on chart",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    items(allSignals, key = { it.id }) { sig ->
                        val isSelected = sig.instrumentSymbol == instrument.symbol
                        val sigIsBuy = sig.action == TradeAction.BUY_SNIPER
                        val sigIsSell = sig.action == TradeAction.SELL_SNIPER
                        val sigIsPotential = sig.state == SignalState.POTENTIAL_DEVELOPING
                        val sigIsFailed = sig.state == SignalState.FAILED || sig.state == SignalState.COMPLETED_SL
                        val sigIsNoTrade = sig.action == TradeAction.NO_TRADE

                        val chipColor = when {
                            sigIsNoTrade -> GoldPrimary
                            sigIsPotential -> CyanLight
                            sigIsBuy -> BullishGreen
                            sigIsSell -> BearishRed
                            sigIsFailed -> BearishRedLight
                            else -> TextMuted
                        }

                        Surface(
                            onClick = { onSelectSignal?.invoke(sig) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ObsidianSurface else ObsidianBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) chipColor else ObsidianCardBorder
                            ),
                            modifier = Modifier.testTag("tv_signal_chip_${sig.instrumentSymbol}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(chipColor)
                                )
                                Text(
                                    text = sig.instrumentSymbol,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = if (isSelected) TextWhite else TextSecondary
                                )
                                Text(
                                    text = when {
                                        sigIsBuy -> "BUY"
                                        sigIsSell -> "SELL"
                                        sigIsPotential -> "POT"
                                        sigIsFailed -> "FAIL"
                                        else -> "FLAT"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = chipColor
                                )
                                if (!sigIsNoTrade && sig.riskRewardRatio.isNotBlank()) {
                                    Text(
                                        text = sig.riskRewardRatio,
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // TradingView Interactive Chart Container with On-Chart Overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 500.dp else 360.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianBackground)
        ) {
            val chartTag = "${instrument.tradingViewSymbol}_${timeframe.tvInterval}"

            // Embedded Real-Time TradingView Engine
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            setSupportZoom(true)
                            builtInZoomControls = false
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }
                        }
                        webChromeClient = WebChromeClient()
                        tag = chartTag
                        loadDataWithBaseURL(
                            "https://s.tradingview.com",
                            fastWidgetHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    if (webView.tag != chartTag) {
                        webView.tag = chartTag
                        webView.loadDataWithBaseURL(
                            "https://s.tradingview.com",
                            fastWidgetHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // ON-CHART FLOATING SIGNAL HUD (Directly overlaid on TradingView candles)
            if (showLevelsOverlay && signal != null) {
                // Top-Left Floating Signal Ribbon
                Surface(
                    color = ObsidianBackground.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primarySignalColor.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .testTag("on_chart_signal_ribbon")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(primarySignalColor)
                            )
                            Text(
                                text = when {
                                    isBuy -> "BUY SNIPER (${signal.riskRewardRatio})"
                                    isSell -> "SELL SNIPER (${signal.riskRewardRatio})"
                                    isPotential -> "DEVELOPING SETUP"
                                    isFailed -> "FAILED (SL INVALIDATED)"
                                    else -> "NO TRADE (PROTECTED)"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primarySignalColor
                            )
                            Text(
                                text = "• ${signal.confluenceScore}% ICT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldLight
                            )
                        }

                        if (!isNoTrade && signal.entryPrice > 0.0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            val pnlStr = if (signal.liveCurrentPrice > 0.0) {
                                val isProf = signal.floatingPips >= 0.0
                                " | LIVE ${if (isProf) "+" else ""}${signal.floatingPips}p"
                            } else ""
                            Text(
                                text = "ENTRY ${instrument.formatPrice(signal.entryPrice)} | SL ${instrument.formatPrice(signal.stopLossPrice)}$pnlStr",
                                fontSize = 9.sp,
                                color = if (signal.floatingPips > 0) BullishGreenLight else if (signal.floatingPips < 0) BearishRedLight else TextWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Right-Side Target Price Badges (aligned to TradingView price axis)
                if (!isNoTrade && signal.entryPrice > 0.0) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // TP2 Badge
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight)
                        ) {
                            Text(
                                text = "TP2 ${instrument.formatPrice(signal.takeProfit2Price)} (+${signal.tp2Pips.toInt()}p)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBackground,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        // TP1 Badge
                        Surface(
                            color = BullishGreen.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreenLight)
                        ) {
                            Text(
                                text = "TP1 ${instrument.formatPrice(signal.takeProfit1Price)} (+${signal.tp1Pips.toInt()}p)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBackground,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        // Entry Badge
                        Surface(
                            color = CyanDark.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanLight)
                        ) {
                            Text(
                                text = "ENTRY ${instrument.formatPrice(signal.entryPrice)}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        // SL Invalidation Badge
                        Surface(
                            color = BearishRed.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BearishRedLight)
                        ) {
                            Text(
                                text = "SL ${instrument.formatPrice(signal.stopLossPrice)} (-${signal.riskPips.toInt()}p)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Loading Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBackground.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = GoldPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            // Bottom-Left Live Status Badge
            Surface(
                color = ObsidianBackground.copy(alpha = 0.82f),
                shape = RoundedCornerShape(bottomStart = 10.dp, topEnd = 6.dp),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(BullishGreen)
                    )
                    Text(
                        text = "LIVE TV FEED • ICT/SMC SYNCED",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Data Transparency Footer
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "Live TradingView engine • Top-down institutional SMC setup projected on chart.",
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }

            Text(
                text = "${timeframe.title} candles",
                fontSize = 9.sp,
                color = CyanLight,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Fast embed using TradingView's direct widget iframe.
 * Preconnects and caches to achieve sub-second loading time without reload delays.
 */
private fun generateFastTradingViewEmbed(symbol: String, interval: String): String {
    val cleanSymbol = java.net.URLEncoder.encode(symbol, "UTF-8")
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="dns-prefetch" href="https://s.tradingview.com">
            <link rel="preconnect" href="https://s.tradingview.com" crossorigin>
            <style>
                body, html {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    background-color: #0A0E17;
                    overflow: hidden;
                }
                iframe {
                    border: none;
                    width: 100%;
                    height: 100%;
                    display: block;
                }
            </style>
        </head>
        <body>
            <iframe
                src="https://s.tradingview.com/widgetembed/?symbol=$cleanSymbol&interval=$interval&theme=dark&style=1&timezone=Etc%2FUTC&locale=en&hide_side_toolbar=0&allow_symbol_change=1&save_image=0"
                allowtransparency="true"
                scrolling="no"
                allowfullscreen>
            </iframe>
        </body>
        </html>
    """.trimIndent()
}
