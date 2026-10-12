package com.example.model

/**
 * Supported market asset categories.
 */
enum class AssetCategory(val displayName: String) {
    ALL("All Assets"),
    FOREX("Forex"),
    COMMODITIES("Commodities"),
    INDICES("Indices"),
    CRYPTO("Crypto")
}

/**
 * 8 Institutional Timeframes for strict Top-Down Confluence Analysis.
 */
enum class Timeframe(
    val code: String,
    val title: String,
    val isHTF: Boolean,
    val role: String
) {
    MONTHLY("MN", "Monthly", true, "Macro Directional Bias & Yearly Order Flow"),
    WEEKLY("W1", "Weekly", true, "Interbank Institutional Narrative & Swing POIs"),
    DAILY("D1", "Daily", true, "Major Break of Structure (BOS) & Trend Filter"),
    H4("4H", "4-Hour", true, "High-Probability Institutional Order Blocks & FVGs"),
    H1("1H", "1-Hour", false, "Intermediate Market Structure & Liquidity Pools"),
    M30("30M", "30-Min", false, "Session High/Low Inducement & Range Identification"),
    M15("15M", "15-Min", false, "Change of Character (CHoCH) & Liquidity Sweeps"),
    M5("5M", "5-Min", false, "Sniper Entry Trigger & Tight Risk Mitigation")
}

/**
 * Trend direction across timeframes.
 */
enum class TrendDirection(val label: String) {
    BULLISH("Bullish"),
    BEARISH("Bearish"),
    RANGING("Consolidating / Ranging")
}

/**
 * Candlestick data for interactive charts.
 */
data class Candlestick(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long = 1000L
) {
    val isBullish: Boolean get() = close >= open
}

/**
 * Market instrument specification with live tick properties.
 */
data class MarketInstrument(
    val symbol: String,
    val name: String,
    val category: AssetCategory,
    val currentPrice: Double,
    val bidPrice: Double,
    val askPrice: Double,
    val spreadPips: Double,
    val change24hPercent: Double,
    val high24h: Double,
    val low24h: Double,
    val pipDecimals: Int = 4, // 2 for JPY/Gold, 4 for EUR/USD, 1 for US100
    val pipSize: Double = 0.0001,
    val isWatchlist: Boolean = false
) {
    fun formatPrice(price: Double): String {
        return "%.${pipDecimals}f".format(price)
    }

    fun calculatePips(diff: Double): Double {
        return diff / pipSize
    }
}

/**
 * Confluence status for an individual timeframe in the Top-Down analysis matrix.
 */
data class TimeframeConfluence(
    val timeframe: Timeframe,
    val trend: TrendDirection,
    val marketStructure: String, // e.g. "BOS Bullish", "CHoCH Confirmed", "Internal Sweep"
    val institutionalPOI: String, // e.g. "Unmitigated Bullish OB", "4H FVG", "Sell-side Liquidity Pool"
    val isAlignedWithHTF: Boolean,
    val confluenceWeightScore: Int // Out of 10-15 points
)

/**
 * Fundamental economic context and news risk evaluation.
 */
data class FundamentalContext(
    val centralBankBias: String, // e.g. "Federal Reserve Hawkish / ECB Neutral"
    val sentimentRating: String, // "Strong Bullish Catalyst", "Neutral", "High Volatility Warning"
    val upcomingEvents: String, // e.g. "No High Impact Red-Folder News (Safe to Trade)"
    val dxyCorrelation: String, // e.g. "Inverse DXY Alignment Confirmed"
    val isSafeForExecution: Boolean
)

/**
 * Smart Money Concepts (SMC) & Institutional Manipulation overlay markers.
 */
data class SmartMoneyMarker(
    val type: MarkerType,
    val startPrice: Double,
    val endPrice: Double,
    val label: String,
    val description: String,
    val isMitigated: Boolean = false
)

enum class MarkerType {
    ORDER_BLOCK_BULLISH,
    ORDER_BLOCK_BEARISH,
    FAIR_VALUE_GAP,
    BUY_SIDE_LIQUIDITY,
    SELL_SIDE_LIQUIDITY,
    JUDAS_SWING_SWEEP,
    BREAK_OF_STRUCTURE,
    CHANGE_OF_CHARACTER
}

/**
 * Signal trigger state.
 */
enum class SignalState {
    ACTIVE_SNIPER,       // Confluence >= 88%, sniper entry active
    POTENTIAL_DEVELOPING, // High potential, waiting for liquidity sweep or 5M trigger
    NO_SETUP_PROTECTED,  // No high-probability confluence: disciplined capital protection
    COMPLETED_TP1,       // Closed at TP 1 (1:3 RR)
    COMPLETED_TP2,       // Closed at TP 2 (1:5 RR)
    COMPLETED_SL         // Stop Loss hit
}

enum class TradeAction {
    BUY_SNIPER,
    SELL_SNIPER
}

/**
 * Sniper Trading Signal model.
 */
data class SniperSignal(
    val id: String,
    val instrumentSymbol: String,
    val instrumentName: String,
    val action: TradeAction,
    val state: SignalState,
    val entryPrice: Double,
    val stopLossPrice: Double,
    val takeProfit1Price: Double, // 1:3 RR
    val takeProfit2Price: Double, // 1:5 RR
    val riskPips: Double,
    val tp1Pips: Double,
    val tp2Pips: Double,
    val riskRewardRatio: String, // "1:3.0" or "1:5.0"
    val confluenceScore: Int, // 0 - 100% (Sniper triggers require >= 88%)
    val htfBias: String, // e.g. "Monthly & Daily Bullish Flow"
    val ltfTrigger: String, // e.g. "5M Liquidity Sweep into 15M Bullish Order Block"
    val manipulationNote: String, // e.g. "Asian Lows Swept at London Open (Judas Swing)"
    val fundamentalClearance: String, // e.g. "US Dollar weakness post-FOMC minutes"
    val timeframesAlignment: List<TimeframeConfluence>,
    val smcMarkers: List<SmartMoneyMarker>,
    val timestamp: String,
    val realizedGainPips: Double = 0.0,
    val realizedRMultiple: String = "",
    val exitDate: String = ""
)
