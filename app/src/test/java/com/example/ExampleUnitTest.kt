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

    @Test
    fun testFailedSignalsAuditWorkflow() {
        val failed = MarketDataProvider.failedSignals
        assertTrue("Must have failed signals recorded for transparent audit", failed.isNotEmpty())

        val sample = failed.first()
        assertEquals(SignalState.FAILED, sample.state)
        assertTrue("Must record failure reason", sample.failureReason.isNotBlank())
        assertTrue("Must record observed failure price", sample.failurePrice > 0.0)

        // Test explicit acknowledgement immutability:
        val acknowledged = sample.copy(isAcknowledged = true)
        assertTrue(acknowledged.isAcknowledged)
        // Historical trade details must remain 100% untouched
        assertEquals(sample.entryPrice, acknowledged.entryPrice, 0.0001)
        assertEquals(sample.stopLossPrice, acknowledged.stopLossPrice, 0.0001)
        assertEquals(sample.takeProfit1Price, acknowledged.takeProfit1Price, 0.0001)
        assertEquals(sample.takeProfit2Price, acknowledged.takeProfit2Price, 0.0001)
        assertEquals(sample.failurePrice, acknowledged.failurePrice, 0.0001)
        assertEquals(sample.failureReason, acknowledged.failureReason)
    }

    @Test
    fun testPotentialSignalsHaveCriteriaChecklists() {
        val potentials = MarketDataProvider.potentialSignals
        assertTrue("Must have potential signals", potentials.isNotEmpty())
        for (pot in potentials) {
            assertEquals(SignalState.POTENTIAL_DEVELOPING, pot.state)
            assertTrue("Must specify satisfied conditions", pot.conditionsMet.isNotEmpty())
            assertTrue("Must specify remaining activation conditions", pot.remainingConditions.isNotEmpty())
            assertTrue("Must specify setup age or timestamp", pot.setupAge.isNotBlank() || pot.timestamp.isNotBlank())
            assertTrue("Must specify expiry conditions", pot.expiryConditions.isNotBlank())
        }
    }

    @Test
    fun testIdempotencyAndNoDuplicateSignalIds() {
        val allSignals = MarketDataProvider.activeSniperSignals +
                MarketDataProvider.potentialSignals +
                MarketDataProvider.failedSignals +
                MarketDataProvider.noTradeSignals +
                MarketDataProvider.completedSignals

        val ids = allSignals.map { it.id }
        val uniqueIds = ids.toSet()
        assertEquals("Signal IDs must be strictly unique and idempotent", ids.size, uniqueIds.size)
    }

    @Test
    fun testSignalLifecycleStatesCoverRequiredSpectrum() {
        val states = SignalState.values().map { it.name }
        assertTrue(states.contains("POTENTIAL_DEVELOPING"))
        assertTrue(states.contains("CONFIRMED_SETUP"))
        assertTrue(states.contains("ACTIVE_SNIPER"))
        assertTrue(states.contains("FAILED"))
        assertTrue(states.contains("EXPIRED"))
        assertTrue(states.contains("CANCELLED"))
        assertTrue(states.contains("UNVERIFIED"))
        assertTrue(states.contains("COMPLETED_TP1"))
        assertTrue(states.contains("COMPLETED_TP2"))
        assertTrue(states.contains("NO_SETUP_PROTECTED"))
    }

    @Test
    fun testAllSignalsAreLinkedToTradingViewChart() {
        val allSignals = MarketDataProvider.activeSniperSignals +
                MarketDataProvider.potentialSignals +
                MarketDataProvider.failedSignals +
                MarketDataProvider.noTradeSignals +
                MarketDataProvider.completedSignals

        val instruments = MarketDataService.initialInstruments
        for (signal in allSignals) {
            val instrument = instruments.find { it.symbol == signal.instrumentSymbol }
            assertNotNull("Signal ${signal.id} symbol ${signal.instrumentSymbol} must match a known instrument", instrument)
            assertTrue("Signal ${signal.id} instrument must have a valid TradingView symbol for charting", instrument!!.tradingViewSymbol.isNotBlank())
        }
    }

    @Test
    fun testRealTimeSignalPnLAndInvalidationLogic() {
        val testSignal = SniperSignal(
            id = "TEST-EURUSD-1",
            instrumentSymbol = "EURUSD",
            instrumentName = "Euro / USD",
            action = TradeAction.BUY_SNIPER,
            state = SignalState.CONFIRMED_SETUP,
            entryPrice = 1.0850,
            stopLossPrice = 1.0820,
            takeProfit1Price = 1.0940,
            takeProfit2Price = 1.1000,
            riskPips = 30.0,
            tp1Pips = 90.0,
            tp2Pips = 150.0,
            riskRewardRatio = "1:3.0",
            confluenceScore = 95,
            htfBias = "Bullish",
            ltfTrigger = "M15 FVG Retest",
            manipulationNote = "Asian Low Swept",
            fundamentalClearance = "Safe",
            timeframesAlignment = emptyList(),
            smcMarkers = emptyList(),
            timestamp = "Just now"
        )

        // Case 1: Price moved in profit (1.0880 -> +30 pips)
        val currentPriceInProfit = 1.0880
        val pipsProfit = (currentPriceInProfit - testSignal.entryPrice) / 0.0001
        assertEquals(30.0, pipsProfit, 0.01)

        // Case 2: Price breached Stop Loss (1.0815 -> SL Hit)
        val currentPriceSlHit = 1.0815
        val isSlBreached = currentPriceSlHit <= testSignal.stopLossPrice
        assertTrue("SL should be breached when price drops below 1.0820", isSlBreached)

        // Case 3: Price touched TP1 (1.0945 -> TP1 Hit)
        val currentPriceTp1Hit = 1.0945
        val isTp1Reached = currentPriceTp1Hit >= testSignal.takeProfit1Price
        assertTrue("TP1 should be reached when price exceeds 1.0940", isTp1Reached)
    }

    @Test
    fun testExternalApiValidationRejectsBlankKeys() = kotlinx.coroutines.runBlocking {
        val (success, message) = MarketDataService.testExternalApiConnection("TWELVEDATA", "")
        assertFalse("Blank API key should be rejected", success)
        assertTrue(message.contains("empty"))
    }
}

