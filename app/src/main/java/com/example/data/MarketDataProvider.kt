package com.example.data

import com.example.model.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

object MarketDataProvider {

    val defaultInstruments = listOf(
        MarketInstrument(
            symbol = "XAUUSD",
            name = "Gold / US Dollar",
            category = AssetCategory.COMMODITIES,
            currentPrice = 2654.80,
            bidPrice = 2654.65,
            askPrice = 2654.95,
            spreadPips = 3.0,
            change24hPercent = 1.42,
            high24h = 2668.50,
            low24h = 2638.20,
            pipDecimals = 2,
            pipSize = 0.1,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "AUDUSD",
            name = "Australian Dollar / USD",
            category = AssetCategory.FOREX,
            currentPrice = 0.6728,
            bidPrice = 0.67272,
            askPrice = 0.67288,
            spreadPips = 1.6,
            change24hPercent = -0.38,
            high24h = 0.6765,
            low24h = 0.6710,
            pipDecimals = 4,
            pipSize = 0.0001,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "GBPUSD",
            name = "British Pound / USD",
            category = AssetCategory.FOREX,
            currentPrice = 1.3065,
            bidPrice = 1.30642,
            askPrice = 1.30658,
            spreadPips = 1.6,
            change24hPercent = 0.54,
            high24h = 1.3115,
            low24h = 1.3020,
            pipDecimals = 4,
            pipSize = 0.0001,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "GBPAUD",
            name = "British Pound / Australian Dollar",
            category = AssetCategory.FOREX,
            currentPrice = 1.9422,
            bidPrice = 1.94200,
            askPrice = 1.94240,
            spreadPips = 4.0,
            change24hPercent = 0.92,
            high24h = 1.9480,
            low24h = 1.9350,
            pipDecimals = 4,
            pipSize = 0.0001,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "US100",
            name = "Nasdaq 100 Index",
            category = AssetCategory.INDICES,
            currentPrice = 20385.50,
            bidPrice = 20384.50,
            askPrice = 20386.50,
            spreadPips = 2.0,
            change24hPercent = 1.15,
            high24h = 20450.00,
            low24h = 20210.00,
            pipDecimals = 1,
            pipSize = 1.0,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "GBPJPY",
            name = "British Pound / Japanese Yen",
            category = AssetCategory.FOREX,
            currentPrice = 194.85,
            bidPrice = 194.83,
            askPrice = 194.87,
            spreadPips = 4.0,
            change24hPercent = 0.78,
            high24h = 195.30,
            low24h = 193.90,
            pipDecimals = 2,
            pipSize = 0.01,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "USDJPY",
            name = "US Dollar / Japanese Yen",
            category = AssetCategory.FOREX,
            currentPrice = 149.12,
            bidPrice = 149.105,
            askPrice = 149.135,
            spreadPips = 3.0,
            change24hPercent = 0.22,
            high24h = 149.65,
            low24h = 148.70,
            pipDecimals = 2,
            pipSize = 0.01,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "USOIL",
            name = "WTI Crude Oil",
            category = AssetCategory.COMMODITIES,
            currentPrice = 75.40,
            bidPrice = 75.37,
            askPrice = 75.43,
            spreadPips = 6.0,
            change24hPercent = -1.10,
            high24h = 76.80,
            low24h = 74.90,
            pipDecimals = 2,
            pipSize = 0.01,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "SILVER",
            name = "Silver / US Dollar (XAGUSD)",
            category = AssetCategory.COMMODITIES,
            currentPrice = 31.85,
            bidPrice = 31.83,
            askPrice = 31.87,
            spreadPips = 4.0,
            change24hPercent = 2.15,
            high24h = 32.10,
            low24h = 31.20,
            pipDecimals = 2,
            pipSize = 0.01,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "NZDJPY",
            name = "New Zealand Dollar / JPY",
            category = AssetCategory.FOREX,
            currentPrice = 91.24,
            bidPrice = 91.215,
            askPrice = 91.265,
            spreadPips = 5.0,
            change24hPercent = -0.45,
            high24h = 91.80,
            low24h = 90.95,
            pipDecimals = 2,
            pipSize = 0.01,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "EURJPY",
            name = "Euro / Japanese Yen",
            category = AssetCategory.FOREX,
            currentPrice = 163.40,
            bidPrice = 163.38,
            askPrice = 163.42,
            spreadPips = 4.0,
            change24hPercent = 0.35,
            high24h = 163.95,
            low24h = 162.80,
            pipDecimals = 2,
            pipSize = 0.01,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "EURCHF",
            name = "Euro / Swiss Franc",
            category = AssetCategory.FOREX,
            currentPrice = 0.9415,
            bidPrice = 0.94135,
            askPrice = 0.94165,
            spreadPips = 3.0,
            change24hPercent = 0.05,
            high24h = 0.9440,
            low24h = 0.9390,
            pipDecimals = 4,
            pipSize = 0.0001,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "GBPCHF",
            name = "British Pound / Swiss Franc",
            category = AssetCategory.FOREX,
            currentPrice = 1.1240,
            bidPrice = 1.1237,
            askPrice = 1.1243,
            spreadPips = 6.0,
            change24hPercent = 0.18,
            high24h = 1.1285,
            low24h = 1.1210,
            pipDecimals = 4,
            pipSize = 0.0001,
            isWatchlist = false
        ),
        MarketInstrument(
            symbol = "BTCUSD",
            name = "Bitcoin / US Dollar",
            category = AssetCategory.CRYPTO,
            currentPrice = 64820.00,
            bidPrice = 64810.00,
            askPrice = 64830.00,
            spreadPips = 20.0,
            change24hPercent = 2.85,
            high24h = 65400.00,
            low24h = 63100.00,
            pipDecimals = 1,
            pipSize = 1.0,
            isWatchlist = true
        ),
        MarketInstrument(
            symbol = "SPX500",
            name = "S&P 500 Index",
            category = AssetCategory.INDICES,
            currentPrice = 5815.20,
            bidPrice = 5814.70,
            askPrice = 5815.70,
            spreadPips = 1.0,
            change24hPercent = 0.68,
            high24h = 5830.00,
            low24h = 5790.00,
            pipDecimals = 1,
            pipSize = 1.0,
            isWatchlist = false
        )
    )

    fun getConfluenceMatrix(symbol: String): List<TimeframeConfluence> {
        return when (symbol) {
            "XAUUSD" -> listOf(
                TimeframeConfluence(Timeframe.MONTHLY, TrendDirection.BULLISH, "HTF Expansion BOS at $2600", "Monthly Demand Zone", true, 15),
                TimeframeConfluence(Timeframe.WEEKLY, TrendDirection.BULLISH, "Bullish Order Flow Sustained", "Weekly Swing Low Protected", true, 15),
                TimeframeConfluence(Timeframe.DAILY, TrendDirection.BULLISH, "Internal BOS above $2640", "Daily Bullish FVG Tapped", true, 15),
                TimeframeConfluence(Timeframe.H4, TrendDirection.BULLISH, "H4 Mitigation of Discount OB", "Unmitigated 4H Order Block ($2648-$2652)", true, 15),
                TimeframeConfluence(Timeframe.H1, TrendDirection.BULLISH, "H1 Shift & Liquidity Sweep", "Asian Range Low Swept ($2647.50)", true, 10),
                TimeframeConfluence(Timeframe.M30, TrendDirection.BULLISH, "M30 Displacement Candle", "Fair Value Gap Formed ($2651-$2653)", true, 10),
                TimeframeConfluence(Timeframe.M15, TrendDirection.BULLISH, "M15 Market Structure Shift", "Strong Institutional Inflow", true, 10),
                TimeframeConfluence(Timeframe.M5, TrendDirection.BULLISH, "5M CHoCH + FVG Retest", "Sniper Limit Trigger Filled at $2654.00", true, 10)
            )
            "GBPJPY" -> listOf(
                TimeframeConfluence(Timeframe.MONTHLY, TrendDirection.BEARISH, "Major HTF Resistance at 196.00", "Monthly Supply Imbalance", true, 15),
                TimeframeConfluence(Timeframe.WEEKLY, TrendDirection.BEARISH, "Swing High Liquidity Grab", "Weekly Breaker Block", true, 15),
                TimeframeConfluence(Timeframe.DAILY, TrendDirection.BEARISH, "Daily CHoCH Bearish", "Daily Bearish Fair Value Gap", true, 15),
                TimeframeConfluence(Timeframe.H4, TrendDirection.BEARISH, "Rejection at Premium 78.6% OTE", "4H Bearish Order Block (195.20)", true, 15),
                TimeframeConfluence(Timeframe.H1, TrendDirection.BEARISH, "Clean Bearish BOS", "Asian High Judas Swing Swept", true, 10),
                TimeframeConfluence(Timeframe.M30, TrendDirection.BEARISH, "Aggressive Downside Displacement", "M30 Inversion FVG", true, 10),
                TimeframeConfluence(Timeframe.M15, TrendDirection.BEARISH, "M15 Break of Internal Lows", "Sell-Side Liquidity Targeted", true, 10),
                TimeframeConfluence(Timeframe.M5, TrendDirection.BEARISH, "5M Sniper Entry at FVG Retest", "Trigger Price 194.95", true, 10)
            )
            "US100" -> listOf(
                TimeframeConfluence(Timeframe.MONTHLY, TrendDirection.BULLISH, "All-Time High Momentum", "Macro Tech Demand", true, 15),
                TimeframeConfluence(Timeframe.WEEKLY, TrendDirection.BULLISH, "Expansionary Leg", "Weekly Support 20,000", true, 15),
                TimeframeConfluence(Timeframe.DAILY, TrendDirection.BULLISH, "Daily Break of Structure", "Daily FVG Retest", true, 15),
                TimeframeConfluence(Timeframe.H4, TrendDirection.BULLISH, "Discount Pricing 61.8%", "4H Bullish OB at 20,320", true, 15),
                TimeframeConfluence(Timeframe.H1, TrendDirection.BULLISH, "Pre-Market Low Cleared", "New York Open Liquidity Grab", true, 10),
                TimeframeConfluence(Timeframe.M30, TrendDirection.BULLISH, "Displacement upward", "M30 Bullish Fair Value Gap", true, 10),
                TimeframeConfluence(Timeframe.M15, TrendDirection.BULLISH, "M15 CHoCH Confirmed", "Institutional Buying Pressure", true, 10),
                TimeframeConfluence(Timeframe.M5, TrendDirection.BULLISH, "5M Sniper Fill", "Trigger at 20,380.00", true, 10)
            )
            else -> listOf(
                TimeframeConfluence(Timeframe.MONTHLY, TrendDirection.RANGING, "Indecision Wick", "Mid-Range", false, 5),
                TimeframeConfluence(Timeframe.WEEKLY, TrendDirection.BULLISH, "Consolidation Bound", "Weekly Key Level", true, 10),
                TimeframeConfluence(Timeframe.DAILY, TrendDirection.RANGING, "Equal Highs & Lows", "Retail Inducement Zone", false, 5),
                TimeframeConfluence(Timeframe.H4, TrendDirection.BEARISH, "Internal Retracement", "4H Choppy Action", false, 5),
                TimeframeConfluence(Timeframe.H1, TrendDirection.RANGING, "Sideways Chop", "Liquidity Not Purged", false, 5),
                TimeframeConfluence(Timeframe.M30, TrendDirection.RANGING, "Inside Bar Range", "Compression", false, 5),
                TimeframeConfluence(Timeframe.M15, TrendDirection.BEARISH, "Lack of Volume", "Noise", false, 5),
                TimeframeConfluence(Timeframe.M5, TrendDirection.RANGING, "Unclear Market Structure", "Do Not Trade", false, 5)
            )
        }
    }

    /**
     * Active Sniper Signals (Confluence >= 88%, strictly 1:3 to 1:5 RR, no false signals).
     */
    val activeSniperSignals = listOf(
        SniperSignal(
            id = "SIG-GOLD-091",
            instrumentSymbol = "XAUUSD",
            instrumentName = "Gold / US Dollar",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.ACTIVE_SNIPER,
            entryPrice = 2654.00,
            stopLossPrice = 2646.00,
            takeProfit1Price = 2678.00, // 1:3.0 RR (+24.00 diff / 8.00 risk = 3.0)
            takeProfit2Price = 2694.00, // 1:5.0 RR (+40.00 diff / 8.00 risk = 5.0)
            riskPips = 80.0,
            tp1Pips = 240.0,
            tp2Pips = 400.0,
            riskRewardRatio = "1:3.0 & 1:5.0",
            confluenceScore = 96,
            htfBias = "Monthly & Weekly Institutional Bullish Expansion with Clean Liquidity Pools above $2685",
            ltfTrigger = "Asian Session Low ($2647.50) swept into London Open unmitigated 4H Bullish Order Block. 5M CHoCH confirmed with clean Fair Value Gap tap.",
            manipulationNote = "Classic Judas Swing engineered at London Open to trap retail breakout sellers before real institutional upside delivery.",
            fundamentalClearance = "Safe: US Federal Reserve rate cut pricing intact. No high-impact CPI/NFP news in the next 12 hours.",
            timeframesAlignment = getConfluenceMatrix("XAUUSD"),
            smcMarkers = listOf(
                SmartMoneyMarker(MarkerType.ORDER_BLOCK_BULLISH, 2647.0, 2652.0, "4H Bullish OB", "Institutional demand zone", true),
                SmartMoneyMarker(MarkerType.JUDAS_SWING_SWEEP, 2646.5, 2648.0, "London Judas Sweep", "Asian low retail stop-hunt", true),
                SmartMoneyMarker(MarkerType.FAIR_VALUE_GAP, 2652.5, 2654.5, "M15 Fair Value Gap", "Imbalance mitigating into entry", false),
                SmartMoneyMarker(MarkerType.BUY_SIDE_LIQUIDITY, 2678.0, 2680.0, "BSL Pool (TP1)", "Equal highs retail liquidity pool", false),
                SmartMoneyMarker(MarkerType.BUY_SIDE_LIQUIDITY, 2694.0, 2696.0, "Major BSL (TP2)", "Weekly buy-side liquidity target", false)
            ),
            timestamp = "Today 08:35 UTC"
        ),
        SniperSignal(
            id = "SIG-GBPJPY-092",
            instrumentSymbol = "GBPJPY",
            instrumentName = "British Pound / Japanese Yen",
            action = TradeAction.SELL_SNIPER,
            state = SignalState.ACTIVE_SNIPER,
            entryPrice = 194.95,
            stopLossPrice = 195.35,
            takeProfit1Price = 193.75, // Risk 0.40 -> TP1 +1.20 (1:3.0 RR)
            takeProfit2Price = 192.95, // Risk 0.40 -> TP2 +2.00 (1:5.0 RR)
            riskPips = 40.0,
            tp1Pips = 120.0,
            tp2Pips = 200.0,
            riskRewardRatio = "1:3.0 & 1:5.0",
            confluenceScore = 94,
            htfBias = "Monthly Macro Supply Wall at 196.00 with Weekly Liquidity Sweep of Prev Highs",
            ltfTrigger = "Asian Session Highs pushed into 4H Bearish Order Block. London session initiated swift Displacement downwards leaving M15 FVG.",
            manipulationNote = "False breakout above 195.20 triggered retail breakout buyers, followed immediately by institutional engulfing down.",
            fundamentalClearance = "Safe: Bank of Japan hawkish rhetoric on monetary normalization countering weak UK GDP data.",
            timeframesAlignment = getConfluenceMatrix("GBPJPY"),
            smcMarkers = listOf(
                SmartMoneyMarker(MarkerType.ORDER_BLOCK_BEARISH, 195.10, 195.40, "4H Bearish OB", "Unmitigated institutional supply block", true),
                SmartMoneyMarker(MarkerType.JUDAS_SWING_SWEEP, 195.25, 195.38, "Asian High Purge", "Inducement liquidity cleared", true),
                SmartMoneyMarker(MarkerType.FAIR_VALUE_GAP, 194.80, 195.05, "M15 Bearish FVG", "Sniper entry retest zone", false),
                SmartMoneyMarker(MarkerType.SELL_SIDE_LIQUIDITY, 193.75, 193.60, "SSL Pool (TP1)", "Previous session low target", false),
                SmartMoneyMarker(MarkerType.SELL_SIDE_LIQUIDITY, 192.95, 192.80, "HTF SSL (TP2)", "Weekly resting sell-side stops", false)
            ),
            timestamp = "Today 10:12 UTC"
        ),
        SniperSignal(
            id = "SIG-US100-093",
            instrumentSymbol = "US100",
            instrumentName = "Nasdaq 100 Index",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.ACTIVE_SNIPER,
            entryPrice = 20380.00,
            stopLossPrice = 20330.00,
            takeProfit1Price = 20530.00, // Risk 50.0 -> TP1 +150.0 (1:3.0 RR)
            takeProfit2Price = 20630.00, // Risk 50.0 -> TP2 +250.0 (1:5.0 RR)
            riskPips = 50.0,
            tp1Pips = 150.0,
            tp2Pips = 250.0,
            riskRewardRatio = "1:3.0 & 1:5.0",
            confluenceScore = 92,
            htfBias = "Daily & Weekly Bullish Order Flow in Price Discovery Zone",
            ltfTrigger = "Pre-market liquidity pool swept during New York 09:30 AM Killzone. Immediate M5 Change of Character (CHoCH) with aggressive buying volume.",
            manipulationNote = "Engineered morning dump below 20,340 swept retail stops directly into institutional discount demand.",
            fundamentalClearance = "Safe: Semiconductor earnings beat and stable yields support tech equities.",
            timeframesAlignment = getConfluenceMatrix("US100"),
            smcMarkers = listOf(
                SmartMoneyMarker(MarkerType.ORDER_BLOCK_BULLISH, 20320.0, 20345.0, "4H Bullish OB", "Institutional demand block", true),
                SmartMoneyMarker(MarkerType.JUDAS_SWING_SWEEP, 20325.0, 20335.0, "NY Open Stop Hunt", "Morning sell stops swept", true),
                SmartMoneyMarker(MarkerType.FAIR_VALUE_GAP, 20365.0, 20390.0, "M5 Bullish FVG", "Institutional displacement retest", false),
                SmartMoneyMarker(MarkerType.BUY_SIDE_LIQUIDITY, 20530.0, 20545.0, "Overnight Highs (TP1)", "Target 1 liquidity", false),
                SmartMoneyMarker(MarkerType.BUY_SIDE_LIQUIDITY, 20630.0, 20650.0, "ATH Target (TP2)", "Major institutional runner", false)
            ),
            timestamp = "Today 13:45 UTC"
        )
    )

    /**
     * Potential / Developing Setups (High probability forming, waiting for key liquidity grab or LTF trigger).
     */
    val potentialSignals = listOf(
        SniperSignal(
            id = "POT-AUDUSD-041",
            instrumentSymbol = "AUDUSD",
            instrumentName = "Australian Dollar / USD",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.POTENTIAL_DEVELOPING,
            entryPrice = 0.6705,
            stopLossPrice = 0.6685,
            takeProfit1Price = 0.6765, // Risk 20 pips -> TP1 +60 pips (1:3.0)
            takeProfit2Price = 0.6805, // Risk 20 pips -> TP2 +100 pips (1:5.0)
            riskPips = 20.0,
            tp1Pips = 60.0,
            tp2Pips = 100.0,
            riskRewardRatio = "1:3.0 & 1:5.0",
            confluenceScore = 78,
            htfBias = "Daily Bullish Retracement to Key 61.8% Fibonacci Golden Pocket",
            ltfTrigger = "PENDING: Waiting for Asian Lows (0.6710) to be swept and confirmed by a 5-minute Market Structure Shift (MSS) with displacement.",
            manipulationNote = "Institutional bait: Retail trendline support is currently acting as liquidity inducement. Do NOT enter until trendline is broken and swept.",
            fundamentalClearance = "Caution: RBA minutes due in 18 hours. Awaiting clarity.",
            timeframesAlignment = getConfluenceMatrix("AUDUSD"),
            smcMarkers = listOf(
                SmartMoneyMarker(MarkerType.ORDER_BLOCK_BULLISH, 0.6690, 0.6708, "Daily Bullish OB", "Pending deep discount mitigation", false),
                SmartMoneyMarker(MarkerType.SELL_SIDE_LIQUIDITY, 0.6710, 0.6715, "Asian Lows (Inducement)", "Target for manipulation sweep", false)
            ),
            timestamp = "Monitoring for New York Killzone"
        ),
        SniperSignal(
            id = "POT-BTCUSD-042",
            instrumentSymbol = "BTCUSD",
            instrumentName = "Bitcoin / US Dollar",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.POTENTIAL_DEVELOPING,
            entryPrice = 64200.0,
            stopLossPrice = 63600.0,
            takeProfit1Price = 66000.0, // Risk 600 -> TP1 +1800 (1:3.0)
            takeProfit2Price = 67200.0, // Risk 600 -> TP2 +3000 (1:5.0)
            riskPips = 60.0,
            tp1Pips = 180.0,
            tp2Pips = 300.0,
            riskRewardRatio = "1:3.0 & 1:5.0",
            confluenceScore = 81,
            htfBias = "Weekly Bullish Symmetrical Coil near $65k",
            ltfTrigger = "PENDING: Awaiting 4H Fair Value Gap fill at $64,200 followed by 15M Bullish Displacement candle.",
            manipulationNote = "Weekend low volume consolidation often lures trapped long breakout traders. We wait for weekend liquidity flush.",
            fundamentalClearance = "Safe: ETF net inflows positive +$280M yesterday.",
            timeframesAlignment = getConfluenceMatrix("BTCUSD"),
            smcMarkers = listOf(
                SmartMoneyMarker(MarkerType.FAIR_VALUE_GAP, 63900.0, 64300.0, "4H Imbalance Zone", "Unfilled buyer imbalance", false)
            ),
            timestamp = "Awaiting 4H Imbalance Fill"
        ),
        SniperSignal(
            id = "POT-GBPAUD-043",
            instrumentSymbol = "GBPAUD",
            instrumentName = "British Pound / Australian Dollar",
            action = TradeAction.SELL_SNIPER,
            state = SignalState.POTENTIAL_DEVELOPING,
            entryPrice = 1.9480,
            stopLossPrice = 1.9520,
            takeProfit1Price = 1.9360, // Risk 40 pips -> TP1 +120 pips (1:3.0)
            takeProfit2Price = 1.9280, // Risk 40 pips -> TP2 +200 pips (1:5.0)
            riskPips = 40.0,
            tp1Pips = 120.0,
            tp2Pips = 200.0,
            riskRewardRatio = "1:3.0 & 1:5.0",
            confluenceScore = 76,
            htfBias = "Weekly Bearish Key Resistance at 1.9500 Institutional Psych Level",
            ltfTrigger = "PENDING: Waiting for final push into 1.9480-1.9500 to wipe out early short sellers before London close.",
            manipulationNote = "Market makers engineering equal highs to entice retail breakout orders as exit liquidity.",
            fundamentalClearance = "Monitored: UK Services PMI upcoming.",
            timeframesAlignment = getConfluenceMatrix("GBPAUD"),
            smcMarkers = listOf(
                SmartMoneyMarker(MarkerType.ORDER_BLOCK_BEARISH, 1.9475, 1.9510, "Daily Breaker Block", "Anticipated mitigation", false)
            ),
            timestamp = "Stalking Resistance Pool"
        )
    )

    /**
     * Completed Signals Ledger (Transparent institutional track record showing verified outcomes).
     */
    val completedSignals = listOf(
        SniperSignal(
            id = "COMP-GOLD-088",
            instrumentSymbol = "XAUUSD",
            instrumentName = "Gold / US Dollar",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.COMPLETED_TP2,
            entryPrice = 2622.00,
            stopLossPrice = 2614.00,
            takeProfit1Price = 2646.00,
            takeProfit2Price = 2662.00,
            riskPips = 80.0,
            tp1Pips = 240.0,
            tp2Pips = 400.0,
            riskRewardRatio = "1:5.0",
            confluenceScore = 98,
            htfBias = "Monthly Trend continuation from $2600 macro psychological support",
            ltfTrigger = "London Open Judas low sweep directly into 1H Bullish OB",
            manipulationNote = "Engineered sell stop purge cleared 2616 retail liquidity",
            fundamentalClearance = "FOMC dovish dot plot reaction",
            timeframesAlignment = getConfluenceMatrix("XAUUSD"),
            smcMarkers = emptyList(),
            timestamp = "Oct 08 07:15 UTC",
            realizedGainPips = 400.0,
            realizedRMultiple = "+5.0 R",
            exitDate = "Oct 09 14:20 UTC (TP2 Hit)"
        ),
        SniperSignal(
            id = "COMP-GBPUSD-087",
            instrumentSymbol = "GBPUSD",
            instrumentName = "British Pound / USD",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.COMPLETED_TP1,
            entryPrice = 1.2980,
            stopLossPrice = 1.2950,
            takeProfit1Price = 1.3070,
            takeProfit2Price = 1.3130,
            riskPips = 30.0,
            tp1Pips = 90.0,
            tp2Pips = 150.0,
            riskRewardRatio = "1:3.0",
            confluenceScore = 95,
            htfBias = "Daily Bullish FVG filled at 1.2975",
            ltfTrigger = "5M CHoCH after NY session low purge",
            manipulationNote = "Retail double-bottom trap breached before rapid institutional rally",
            fundamentalClearance = "Bank of England rate hold confirmation",
            timeframesAlignment = getConfluenceMatrix("GBPUSD"),
            smcMarkers = emptyList(),
            timestamp = "Oct 06 13:30 UTC",
            realizedGainPips = 90.0,
            realizedRMultiple = "+3.0 R",
            exitDate = "Oct 07 10:45 UTC (TP1 Hit, Partial Closed)"
        ),
        SniperSignal(
            id = "COMP-US100-086",
            instrumentSymbol = "US100",
            instrumentName = "Nasdaq 100 Index",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.COMPLETED_TP2,
            entryPrice = 19950.00,
            stopLossPrice = 19890.00,
            takeProfit1Price = 20130.00,
            takeProfit2Price = 20250.00,
            riskPips = 60.0,
            tp1Pips = 180.0,
            tp2Pips = 300.0,
            riskRewardRatio = "1:5.0",
            confluenceScore = 97,
            htfBias = "Weekly Demand Zone tap at 19,900",
            ltfTrigger = "Opening bell Judas swing swept into 15M OB with heavy tape volume",
            manipulationNote = "Aggressive pre-market short squeeze engineered",
            fundamentalClearance = "CPI in line with expectations",
            timeframesAlignment = getConfluenceMatrix("US100"),
            smcMarkers = emptyList(),
            timestamp = "Oct 03 13:30 UTC",
            realizedGainPips = 300.0,
            realizedRMultiple = "+5.0 R",
            exitDate = "Oct 04 18:00 UTC (TP2 Hit)"
        ),
        SniperSignal(
            id = "COMP-USDJPY-085",
            instrumentSymbol = "USDJPY",
            instrumentName = "US Dollar / Japanese Yen",
            action = TradeAction.SELL_SNIPER,
            state = SignalState.COMPLETED_TP1,
            entryPrice = 150.20,
            stopLossPrice = 150.55,
            takeProfit1Price = 149.15,
            takeProfit2Price = 148.45,
            riskPips = 35.0,
            tp1Pips = 105.0,
            tp2Pips = 175.0,
            riskRewardRatio = "1:3.0",
            confluenceScore = 91,
            htfBias = "Daily Supply Block at 150.50 psych resistance",
            ltfTrigger = "Asian high sweep followed by clean 15M CHoCH",
            manipulationNote = "Intervention rumors triggered retail buying stop hunt",
            fundamentalClearance = "US Treasury yield pullback",
            timeframesAlignment = getConfluenceMatrix("USDJPY"),
            smcMarkers = emptyList(),
            timestamp = "Sep 29 02:10 UTC",
            realizedGainPips = 105.0,
            realizedRMultiple = "+3.0 R",
            exitDate = "Sep 30 08:15 UTC (TP1 Hit)"
        )
    )

    /**
     * Generates a realistic candlestick series for any pair and timeframe.
     */
    fun generateCandles(
        symbol: String,
        timeframe: Timeframe,
        basePrice: Double,
        count: Int = 40
    ): List<Candlestick> {
        val candles = mutableListOf<Candlestick>()
        val volatility = when (symbol) {
            "XAUUSD" -> 4.5
            "US100" -> 35.0
            "BTCUSD" -> 280.0
            "GBPJPY" -> 0.35
            "USDJPY" -> 0.25
            else -> 0.0015
        } * when (timeframe) {
            Timeframe.MONTHLY -> 8.0
            Timeframe.WEEKLY -> 5.0
            Timeframe.DAILY -> 3.0
            Timeframe.H4 -> 1.8
            Timeframe.H1 -> 1.2
            Timeframe.M30 -> 0.9
            Timeframe.M15 -> 0.6
            Timeframe.M5 -> 0.35
        }

        // Build candles backwards from current price
        val random = Random(symbol.hashCode() + timeframe.ordinal * 100)
        var runningClose = basePrice
        val stepMillis = when (timeframe) {
            Timeframe.M5 -> 5 * 60 * 1000L
            Timeframe.M15 -> 15 * 60 * 1000L
            Timeframe.M30 -> 30 * 60 * 1000L
            Timeframe.H1 -> 60 * 60 * 1000L
            Timeframe.H4 -> 4 * 60 * 60 * 1000L
            Timeframe.DAILY -> 24 * 60 * 60 * 1000L
            Timeframe.WEEKLY -> 7 * 24 * 60 * 60 * 1000L
            Timeframe.MONTHLY -> 30 * 24 * 60 * 60 * 1000L
        }

        val now = System.currentTimeMillis()

        for (i in count - 1 downTo 0) {
            val isBullish = random.nextBoolean()
            val delta = (random.nextDouble() * 0.8 + 0.2) * volatility
            val open: Double
            val close: Double

            if (i == 0) {
                // Latest candle connects to basePrice
                close = runningClose
                open = if (isBullish) close - delta else close + delta
            } else {
                open = runningClose - (if (isBullish) delta else -delta)
                close = runningClose
            }

            val highWick = random.nextDouble() * (volatility * 0.4)
            val lowWick = random.nextDouble() * (volatility * 0.4)
            val high = maxOf(open, close) + highWick
            val low = minOf(open, close) - lowWick

            val timestamp = now - (i * stepMillis)
            candles.add(
                Candlestick(
                    timestamp = timestamp,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = (random.nextInt(800, 3500) * (if (isBullish) 1.2 else 0.9)).toLong()
                )
            )

            runningClose = open
        }

        return candles
    }

    /**
     * Calculates position lot size based on account balance, risk percentage, and stop loss pips.
     */
    fun calculateLotSize(
        accountBalance: Double,
        riskPercent: Double,
        stopLossPips: Double,
        pipValuePerStandardLot: Double = 10.0 // Default $10 per pip on 1.00 lot EURUSD/GBPUSD
    ): Double {
        if (stopLossPips <= 0.0) return 0.01
        val riskAmount = accountBalance * (riskPercent / 100.0)
        val lotSize = riskAmount / (stopLossPips * pipValuePerStandardLot)
        return (lotSize * 100.0).roundToInt() / 100.0
    }
}
