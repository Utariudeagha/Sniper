package com.example

import com.example.data.MarketDataProvider
import com.example.data.MarketDataService
import com.example.data.TechnicalAnalysisEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testRiskLotCalculation_0Point5Percent() {
        val balance = 10000.0
        val riskPercent = 0.5 // $50 risk (default)
        val stopLossPips = 25.0
        val lotSize = MarketDataProvider.calculateLotSize(balance, riskPercent, stopLossPips)
        // $50 / (25 * 10) = 0.20 lots
        assertEquals(0.20, lotSize, 0.01)
    }

    @Test
    fun testRiskLotCalculation_1Percent() {
        val balance = 10000.0
        val riskPercent = 1.0 // $100 risk
        val stopLossPips = 20.0
        val lotSize = MarketDataProvider.calculateLotSize(balance, riskPercent, stopLossPips)
        // $100 / (20 * 10) = 0.50 lots
        assertEquals(0.50, lotSize, 0.01)
    }

    @Test
    fun testConfluenceMatrixContainsAll8Timeframes() {
        val confluences = MarketDataProvider.getConfluenceMatrix("XAUUSD")
        assertEquals(8, confluences.size)
        val expectedCodes = listOf("MN", "W1", "D1", "4H", "1H", "30M", "15M", "5M")
        val actualCodes = confluences.map { it.timeframe.code }
        assertEquals(expectedCodes, actualCodes)
    }

    @Test
    fun testEveryInstrumentHasValidTradingViewSymbol() {
        val instruments = MarketDataService.initialInstruments
        assertTrue(instruments.size >= 14)
        for (inst in instruments) {
            assertTrue("Instrument ${inst.symbol} must have valid TV symbol", inst.tradingViewSymbol.isNotBlank())
            assertTrue("Instrument ${inst.symbol} TV symbol must have prefix", inst.tradingViewSymbol.contains(":"))
        }
    }

    @Test
    fun testTechnicalEngineReturnsNoTradeWhenConfluenceFails() {
        val inst = MarketDataService.initialInstruments.first()
        val mixedConfluence = listOf(
            TimeframeConfluence(Timeframe.MONTHLY, TrendDirection.BULLISH, "Mixed", "None", false, 5),
            TimeframeConfluence(Timeframe.WEEKLY, TrendDirection.BEARISH, "Chop", "None", false, 5),
            TimeframeConfluence(Timeframe.DAILY, TrendDirection.RANGING, "Flat", "None", false, 5),
            TimeframeConfluence(Timeframe.H4, TrendDirection.BULLISH, "Mixed", "None", false, 5)
        )
        val candles = MarketDataProvider.generateCandles(inst.symbol, Timeframe.H1, inst.currentPrice, 20)
        val result = TechnicalAnalysisEngine.qualifySetup(inst, candles, mixedConfluence)

        // Strict rule: If criteria not met, output NO_TRADE
        assertEquals(TradeAction.NO_TRADE, result.action)
        assertEquals(SignalState.NO_SETUP_PROTECTED, result.state)
    }

    @Test
    fun testJournalLedgerTracksLosingTradesTransparently() {
        val metrics = MarketDataProvider.calculateJournalMetrics()
        assertTrue("Must track total signals", metrics.totalSignals > 0)
        assertTrue("Must transparently include losses", metrics.lossCount > 0)
        assertTrue("Must calculate profit factor", metrics.profitFactor > 0.0)
    }

    @Test
    fun testActiveSignalsEnforce1to3MinimumRiskReward() {
        val activeSignals = MarketDataProvider.activeSniperSignals
        for (signal in activeSignals) {
            assertTrue("Signal ${signal.id} must have positive risk", signal.riskPips > 0)
            assertTrue("TP1 must be at least 3x risk", signal.tp1Pips >= signal.riskPips * 2.9)
            assertTrue("TP2 must be at least 5x risk", signal.tp2Pips >= signal.riskPips * 4.9)
        }
    }
}
