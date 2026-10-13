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
    val role: String,
    val tvInterval: String // TradingView widget interval code
) {
    MONTHLY("MN", "Monthly", true, "Macro Directional Bias & Yearly Order Flow", "M"),
    WEEKLY("W1", "Weekly", true, "Interbank Institutional Narrative & Swing POIs", "W"),
    DAILY("D1", "Daily", true, "Major Break of Structure (BOS) & Trend Filter", "D"),
    H4("4H", "4-Hour", true, "High-Probability Institutional Order Blocks & FVGs", "240"),
    H1("1H", "1-Hour", false, "Intermediate Market Structure & Liquidity Pools", "60"),
    M30("30M", "30-Min", false, "Session High/Low Inducement & Range Identification", "30"),
    M15("15M", "15-Min", false, "Change of Character (CHoCH) & Liquidity Sweeps", "15"),
    M5("5M", "5-Min", false, "Sniper Entry Trigger & Tight Risk Mitigation", "5")
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
 * Candlestick data for interactive charts and deterministic calculations.
 */
data class Candlestick(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long = 1000L,
    val isClosed: Boolean = true
) {
    val isBullish: Boolean get() = close >= open
}

/**
 * Market instrument specification with live tick properties and official TradingView symbols.
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
    val tradingViewSymbol: String, // e.g. "OANDA:XAUUSD", "FX:EURUSD", "BINANCE:BTCUSDT"
    val dataProvider: String = "TradingView / Open Interbank Feed",
    val feedStatus: DataFeedStatus = DataFeedStatus.INDICATIVE_REAL_TIME,
    val lastUpdateTimestamp: Long = System.currentTimeMillis(),
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
    val centralBankBias: String,
    val sentimentRating: String,
    val upcomingEvents: String,
    val dxyCorrelation: String,
    val isSafeForExecution: Boolean,
    val noTradeWindowActive: Boolean = false,
    val dataSource: String = "Global Economic Calendar"
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
enum class SignalState(val displayTitle: String) {
    CONFIRMED_SETUP("Confirmed Setup"),
    ACTIVE_SNIPER("Active Execution"),
    POTENTIAL_DEVELOPING("Potential Setup (Developing)"),
    NO_SETUP_PROTECTED("No Trade (Capital Protected)"),
    INVALIDATED("Setup Invalidated"),
    FAILED("Setup Failed (Stop Loss Hit)"),
    EXPIRED("Setup Expired"),
    CANCELLED("Setup Cancelled"),
    UNVERIFIED("Unverified Market Data"),
    COMPLETED_TP1("Completed (TP1 Target Hit)"),
    COMPLETED_TP2("Completed (TP2 Target Hit)"),
    COMPLETED_TP3("Completed (TP3 Runner Hit)"),
    COMPLETED_SL("Completed (Stop Loss Hit)")
}

enum class TradeAction {
    BUY_SNIPER,
    SELL_SNIPER,
    NO_TRADE
}

/**
 * Trading Signal & Plan model.
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
    val takeProfit3Price: Double = 0.0,
    val riskPips: Double,
    val tp1Pips: Double,
    val tp2Pips: Double,
    val riskRewardRatio: String, // "1:3.0", "1:5.0"
    val confluenceScore: Int, // 0 - 100%
    val htfBias: String,
    val ltfTrigger: String,
    val manipulationNote: String,
    val fundamentalClearance: String,
    val invalidationConditions: String = "M5 displacement closing beyond invalidation swing level",
    val timeframesAlignment: List<TimeframeConfluence>,
    val smcMarkers: List<SmartMoneyMarker>,
    val timestamp: String,
    val realizedGainPips: Double = 0.0,
    val realizedRMultiple: String = "",
    val exitDate: String = "",
    val dataSource: String = "TradingView Verified Feed",
    val failureReason: String = "",
    val failurePrice: Double = 0.0,
    val failureTimestamp: String = "",
    val isAcknowledged: Boolean = false,
    val conditionsMet: List<String> = emptyList(),
    val remainingConditions: List<String> = emptyList(),
    val setupAge: String = "",
    val expiryConditions: String = ""
)
