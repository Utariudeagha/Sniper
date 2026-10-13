package com.example.data

import com.example.model.EconomicEvent
import com.example.model.EventImpact
import com.example.model.FundamentalContext

object FundamentalAnalysisService {

    val upcomingEconomicEvents = listOf(
        EconomicEvent(
            id = "EVT-01",
            title = "US CPI Inflation Rate YoY",
            currency = "USD",
            impact = EventImpact.HIGH,
            scheduledTimeUtc = "12:30 UTC",
            forecast = "2.9%",
            previous = "3.1%",
            actual = null,
            affectedInstruments = listOf("XAUUSD", "GBPUSD", "AUDUSD", "US100", "USDJPY", "SPX500"),
            dataSource = "US Bureau of Labor Statistics Feed"
        ),
        EconomicEvent(
            id = "EVT-02",
            title = "FOMC Federal Funds Rate Decision",
            currency = "USD",
            impact = EventImpact.HIGH,
            scheduledTimeUtc = "18:00 UTC",
            forecast = "5.00%",
            previous = "5.25%",
            actual = null,
            affectedInstruments = listOf("XAUUSD", "US100", "GBPUSD", "USDJPY", "SPX500", "BTCUSD"),
            dataSource = "Federal Reserve Board Feed"
        ),
        EconomicEvent(
            id = "EVT-03",
            title = "Bank of Japan Monetary Policy Statement",
            currency = "JPY",
            impact = EventImpact.HIGH,
            scheduledTimeUtc = "03:00 UTC",
            forecast = "0.25%",
            previous = "0.25%",
            actual = null,
            affectedInstruments = listOf("USDJPY", "GBPJPY", "EURJPY", "NZDJPY"),
            dataSource = "Bank of Japan Feed"
        ),
        EconomicEvent(
            id = "EVT-04",
            title = "UK Services PMI (Flash)",
            currency = "GBP",
            impact = EventImpact.MEDIUM,
            scheduledTimeUtc = "08:30 UTC",
            forecast = "52.4",
            previous = "52.1%",
            actual = null,
            affectedInstruments = listOf("GBPUSD", "GBPJPY", "GBPAUD", "GBPCHF"),
            dataSource = "S&P Global Feed"
        ),
        EconomicEvent(
            id = "EVT-05",
            title = "Reserve Bank of Australia Rate Decision",
            currency = "AUD",
            impact = EventImpact.HIGH,
            scheduledTimeUtc = "04:30 UTC",
            forecast = "4.35%",
            previous = "4.35%",
            actual = null,
            affectedInstruments = listOf("AUDUSD", "GBPAUD"),
            dataSource = "Reserve Bank of Australia Feed"
        )
    )

    fun getFundamentalContext(symbol: String): FundamentalContext {
        val relatedEvent = upcomingEconomicEvents.find { it.affectedInstruments.contains(symbol) }
        val isHighImpactPending = relatedEvent?.impact == EventImpact.HIGH

        return FundamentalContext(
            centralBankBias = when {
                symbol.contains("JPY") -> "Bank of Japan Normalization / Hawkish Tone"
                symbol.contains("USD") -> "Federal Reserve Easing Cycle / Balanced Stance"
                symbol.contains("AUD") -> "RBA Neutral to Cautious Hawkish"
                else -> "Neutral Macro Policy Stance"
            },
            sentimentRating = if (isHighImpactPending) "High Volatility Warning (Scheduled Event)" else "Order Flow Clear",
            upcomingEvents = relatedEvent?.let { "${it.title} at ${it.scheduledTimeUtc} (${it.impact.label})" }
                ?: "No High-Impact Red Folder Releases in Next 4 Hours",
            dxyCorrelation = if (symbol.contains("USD")) "Inverse US Dollar (DXY) Liquidity Alignment" else "Cross-Currency Flow",
            isSafeForExecution = !isHighImpactPending,
            noTradeWindowActive = isHighImpactPending,
            dataSource = relatedEvent?.dataSource ?: "Global Economic Calendar Feed"
        )
    }
}
