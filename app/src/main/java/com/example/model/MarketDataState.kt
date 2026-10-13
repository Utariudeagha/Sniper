package com.example.model

/**
 * Data feed freshness status according to regulatory and institutional disclosure standards.
 */
enum class DataFeedStatus(val label: String, val badgeColorHex: Long) {
    LIVE_REAL_TIME("Live Real-Time Feed", 0xFF10B981), // Emerald
    INDICATIVE_REAL_TIME("Indicative Real-Time (Interbank/Free)", 0xFF06B6D4), // Cyan
    DELAYED("Delayed (15m)", 0xFFF59E0B), // Amber
    DATA_UNAVAILABLE("Data Unavailable", 0xFFEF4444) // Red
}

/**
 * Metadata about the active market data connection.
 */
data class DataProviderInfo(
    val providerName: String = "TradingView & Open Interbank API",
    val primarySource: String = "TradingView Advanced Widgets + Binance / OpenER Feed",
    val status: DataFeedStatus = DataFeedStatus.INDICATIVE_REAL_TIME,
    val lastUpdateTimestamp: Long = System.currentTimeMillis(),
    val timezone: String = "UTC",
    val feedLatencyMs: Long = 180L,
    val isProgrammaticFeedAvailable: Boolean = true
) {
    fun getFormattedStatus(): String {
        return "${status.label} • Source: $providerName • Latency: ${feedLatencyMs}ms"
    }
}

/**
 * Fundamental economic event.
 */
data class EconomicEvent(
    val id: String,
    val title: String,
    val currency: String,
    val impact: EventImpact,
    val scheduledTimeUtc: String,
    val forecast: String,
    val previous: String,
    val actual: String? = null,
    val affectedInstruments: List<String>,
    val dataSource: String = "Public Economic Calendar Feed"
)

enum class EventImpact(val label: String) {
    HIGH("High Impact (Red Folder)"),
    MEDIUM("Medium Impact (Orange)"),
    LOW("Low Impact (Yellow)")
}

/**
 * Performance metrics for the persistent Signal Journal.
 */
data class JournalMetrics(
    val totalSignals: Int,
    val winCount: Int,
    val lossCount: Int,
    val breakevenCount: Int,
    val winRatePercent: Double,
    val realizedRExpectancy: Double,
    val profitFactor: Double,
    val maxDrawdownPercent: Double,
    val totalPipsRealized: Double
)
