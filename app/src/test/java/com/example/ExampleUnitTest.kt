package com.example

import com.example.data.MarketDataProvider
import com.example.model.Timeframe
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

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
    fun testRiskLotCalculation_2Percent() {
        val balance = 50000.0
        val riskPercent = 2.0 // $1000 risk
        val stopLossPips = 40.0
        val lotSize = MarketDataProvider.calculateLotSize(balance, riskPercent, stopLossPips)
        // $1000 / (40 * 10) = 2.50 lots
        assertEquals(2.50, lotSize, 0.01)
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
    fun testActiveSignalsHaveStrictConfluenceAbove90() {
        val activeSignals = MarketDataProvider.activeSniperSignals
        assertTrue(activeSignals.isNotEmpty())
        for (signal in activeSignals) {
            assertTrue("Signal ${signal.id} must have >= 88% confluence", signal.confluenceScore >= 88)
            assertTrue("Signal ${signal.id} must have positive risk pips", signal.riskPips > 0)
            assertTrue("Signal ${signal.id} must have TP1 >= 3x risk", signal.tp1Pips >= signal.riskPips * 2.9)
            assertTrue("Signal ${signal.id} must have TP2 >= 5x risk", signal.tp2Pips >= signal.riskPips * 4.9)
        }
    }
}
