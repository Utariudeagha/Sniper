package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.MarketInstrument
import com.example.model.Timeframe
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TradingViewChart(
    instrument: MarketInstrument,
    timeframe: Timeframe,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val fastWidgetHtml = remember(instrument.tradingViewSymbol, timeframe.tvInterval) {
        generateFastTradingViewEmbed(instrument.tradingViewSymbol, timeframe.tvInterval)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianCard, RoundedCornerShape(14.dp))
            .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
            .padding(10.dp)
            .testTag("tradingview_chart_container")
    ) {
        // TradingView Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                            text = "TradingView Real-Time Chart",
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
            }

            IconButton(
                onClick = {
                    webViewInstance?.reload()
                },
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

        // Fast TradingView WebView Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianBackground)
        ) {
            val chartTag = "${instrument.tradingViewSymbol}_${timeframe.tvInterval}"

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
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Data Transparency Note
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "TradingView high-speed engine • Broker data synced with SMC/ICT top-down analysis below.",
                fontSize = 10.sp,
                color = TextMuted
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
