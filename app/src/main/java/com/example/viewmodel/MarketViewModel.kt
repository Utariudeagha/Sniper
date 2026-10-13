package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FundamentalAnalysisService
import com.example.data.MarketDataProvider
import com.example.data.MarketDataService
import com.example.data.TechnicalAnalysisEngine
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen(val title: String) {
    DASHBOARD("Dashboard"),
    LIVE_CHARTS("Live Charts"),
    MARKET_SCANNER("Market Scanner"),
    SIGNAL_CENTER("Signal Center"),
    WATCHLIST("Watchlist"),
    SIGNAL_JOURNAL("Signal Journal"),
    ACADEMY("Trading Academy"),
    SETTINGS("Settings & Data Status")
}

enum class SignalsTab {
    CONFIRMED_SETUPS,
    POTENTIAL_SETUPS,
    FAILED_SETUPS,
    NO_TRADE_STATUS,
    COMPLETED_LEDGER
}

enum class ChartDisplayMode {
    TRADINGVIEW_ADVANCED,
    NATIVE_SMC_CANVAS
}

class MarketViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeSignalsTab = MutableStateFlow(SignalsTab.CONFIRMED_SETUPS)
    val activeSignalsTab: StateFlow<SignalsTab> = _activeSignalsTab.asStateFlow()

    private val _chartDisplayMode = MutableStateFlow(ChartDisplayMode.TRADINGVIEW_ADVANCED)
    val chartDisplayMode: StateFlow<ChartDisplayMode> = _chartDisplayMode.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AssetCategory.ALL)
    val selectedCategory: StateFlow<AssetCategory> = _selectedCategory.asStateFlow()

    private val _watchlistSymbols = MutableStateFlow(
        setOf("XAUUSD", "GBPUSD", "US100", "GBPJPY", "BTCUSD", "AUDUSD")
    )
    val watchlistSymbols: StateFlow<Set<String>> = _watchlistSymbols.asStateFlow()

    private val _instruments = MutableStateFlow(MarketDataService.initialInstruments)
    val instruments: StateFlow<List<MarketInstrument>> = _instruments.asStateFlow()

    private val _selectedInstrument = MutableStateFlow(MarketDataService.initialInstruments.first())
    val selectedInstrument: StateFlow<MarketInstrument> = _selectedInstrument.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow(Timeframe.H4)
    val selectedTimeframe: StateFlow<Timeframe> = _selectedTimeframe.asStateFlow()

    private val _activeSignals = MutableStateFlow(MarketDataProvider.activeSniperSignals)
    val activeSignals: StateFlow<List<SniperSignal>> = _activeSignals.asStateFlow()

    private val _potentialSignals = MutableStateFlow(MarketDataProvider.potentialSignals)
    val potentialSignals: StateFlow<List<SniperSignal>> = _potentialSignals.asStateFlow()

    private val _failedSignals = MutableStateFlow(MarketDataProvider.failedSignals)
    val failedSignals: StateFlow<List<SniperSignal>> = _failedSignals.asStateFlow()

    private val _completedSignals = MutableStateFlow(MarketDataProvider.completedSignals)
    val completedSignals: StateFlow<List<SniperSignal>> = _completedSignals.asStateFlow()

    private val _noTradeSignals = MutableStateFlow(MarketDataProvider.noTradeSignals)
    val noTradeSignals: StateFlow<List<SniperSignal>> = _noTradeSignals.asStateFlow()

    private val _selectedSignal = MutableStateFlow<SniperSignal?>(MarketDataProvider.activeSniperSignals.first())
    val selectedSignal: StateFlow<SniperSignal?> = _selectedSignal.asStateFlow()

    private val _candles = MutableStateFlow<List<Candlestick>>(emptyList())
    val candles: StateFlow<List<Candlestick>> = _candles.asStateFlow()

    private val _confluenceMatrix = MutableStateFlow<List<TimeframeConfluence>>(emptyList())
    val confluenceMatrix: StateFlow<List<TimeframeConfluence>> = _confluenceMatrix.asStateFlow()

    private val _dataProviderInfo = MutableStateFlow(DataProviderInfo())
    val dataProviderInfo: StateFlow<DataProviderInfo> = _dataProviderInfo.asStateFlow()

    private val _journalMetrics = MutableStateFlow(MarketDataProvider.calculateJournalMetrics())
    val journalMetrics: StateFlow<JournalMetrics> = _journalMetrics.asStateFlow()

    private val _fundamentalEvents = MutableStateFlow(FundamentalAnalysisService.upcomingEconomicEvents)
    val fundamentalEvents: StateFlow<List<EconomicEvent>> = _fundamentalEvents.asStateFlow()

    // Risk Settings
    private val _riskPercentPerTrade = MutableStateFlow(0.5) // Default strictly 0.5%
    val riskPercentPerTrade: StateFlow<Double> = _riskPercentPerTrade.asStateFlow()

    private val _accountEquity = MutableStateFlow(10000.0)
    val accountEquity: StateFlow<Double> = _accountEquity.asStateFlow()

    private val _dailyLossLimitPercent = MutableStateFlow(2.0)
    val dailyLossLimitPercent: StateFlow<Double> = _dailyLossLimitPercent.asStateFlow()

    private val _riskDialogSignal = MutableStateFlow<SniperSignal?>(null)
    val riskDialogSignal: StateFlow<SniperSignal?> = _riskDialogSignal.asStateFlow()

    private val _isScanningLive = MutableStateFlow(false)
    val isScanningLive: StateFlow<Boolean> = _isScanningLive.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _customApiProvider = MutableStateFlow("TWELVEDATA")
    val customApiProvider: StateFlow<String> = _customApiProvider.asStateFlow()

    private val _apiTestResult = MutableStateFlow<String?>(null)
    val apiTestResult: StateFlow<String?> = _apiTestResult.asStateFlow()

    init {
        _selectedSignal.value = _activeSignals.value.find { it.instrumentSymbol == _selectedInstrument.value.symbol }
            ?: _potentialSignals.value.find { it.instrumentSymbol == _selectedInstrument.value.symbol }
            ?: MarketDataProvider.noTradeSignals.find { it.instrumentSymbol == _selectedInstrument.value.symbol }
        updateChartAndConfluence(_selectedInstrument.value, _selectedTimeframe.value)
        startMarketDataPolling()
    }

    private fun startMarketDataPolling() {
        viewModelScope.launch {
            while (true) {
                try {
                    val (updatedInstruments, info) = MarketDataService.fetchLiveQuotes(_instruments.value)
                    _instruments.value = updatedInstruments
                    _dataProviderInfo.value = info

                    // Sync currently selected instrument
                    val currentUpdated = updatedInstruments.find { it.symbol == _selectedInstrument.value.symbol }
                    if (currentUpdated != null) {
                        _selectedInstrument.value = currentUpdated
                    }

                    // Dynamically evaluate all signals against live tick prices
                    evaluateLiveSignals(updatedInstruments)
                } catch (e: Exception) {
                    _dataProviderInfo.value = _dataProviderInfo.value.copy(
                        status = DataFeedStatus.INDICATIVE_REAL_TIME
                    )
                }
                delay(3000L) // Polling interval
            }
        }
    }

    private fun evaluateLiveSignals(updatedInstruments: List<MarketInstrument>) {
        val currentActives = _activeSignals.value.toMutableList()
        val currentFailed = _failedSignals.value.toMutableList()
        val currentCompleted = _completedSignals.value.toMutableList()
        val currentPotentials = _potentialSignals.value.toMutableList()

        val newlyFailed = mutableListOf<SniperSignal>()
        val newlyCompleted = mutableListOf<SniperSignal>()
        val stillActive = mutableListOf<SniperSignal>()

        for (sig in currentActives) {
            val inst = updatedInstruments.find { it.symbol == sig.instrumentSymbol }
            if (inst == null) {
                stillActive.add(sig)
                continue
            }
            val price = inst.currentPrice
            val pip = inst.pipSize
            val diff = if (sig.action == TradeAction.BUY_SNIPER) (price - sig.entryPrice) else (sig.entryPrice - price)
            val pips = kotlin.math.round((diff / pip) * 10.0) / 10.0
            val pnlUsd = kotlin.math.round(pips * 10.0 * 0.5 * 100.0) / 100.0

            val isBuy = sig.action == TradeAction.BUY_SNIPER

            // Check Stop Loss breach
            val isSlHit = if (isBuy) price <= sig.stopLossPrice else price >= sig.stopLossPrice
            if (isSlHit) {
                newlyFailed.add(
                    sig.copy(
                        state = SignalState.FAILED,
                        failureReason = "Stop Loss breached at ${inst.formatPrice(price)} (-${sig.riskPips.toInt()} pips)",
                        failurePrice = price,
                        failureTimestamp = "Just now",
                        isAcknowledged = false,
                        liveCurrentPrice = price,
                        floatingPips = -sig.riskPips,
                        floatingPnLUsd = -((sig.riskPips * 10.0 * 0.5 * 100.0).toInt() / 100.0)
                    )
                )
                continue
            }

            // Check TP2 hit
            val isTp2Hit = if (isBuy) price >= sig.takeProfit2Price else price <= sig.takeProfit2Price
            if (isTp2Hit) {
                newlyCompleted.add(
                    sig.copy(
                        state = SignalState.COMPLETED_TP2,
                        realizedGainPips = sig.tp2Pips,
                        realizedRMultiple = "1:5.0",
                        exitDate = "Today",
                        liveCurrentPrice = price,
                        floatingPips = sig.tp2Pips,
                        floatingPnLUsd = (sig.tp2Pips * 10.0 * 0.5 * 100.0).toInt() / 100.0
                    )
                )
                continue
            }

            // Check TP1 hit
            val isTp1Hit = if (isBuy) price >= sig.takeProfit1Price else price <= sig.takeProfit1Price
            val updatedState = if (isTp1Hit) SignalState.COMPLETED_TP1 else sig.state

            stillActive.add(
                sig.copy(
                    state = updatedState,
                    liveCurrentPrice = price,
                    floatingPips = pips,
                    floatingPnLUsd = pnlUsd,
                    timestamp = "Live Sync • Just now"
                )
            )
        }

        // Potential signals: check if price tapped the entry zone (within 2.5 pips of entryPrice)
        val remainingPotentials = mutableListOf<SniperSignal>()
        for (pot in currentPotentials) {
            val inst = updatedInstruments.find { it.symbol == pot.instrumentSymbol }
            if (inst != null) {
                val price = inst.currentPrice
                val pip = inst.pipSize
                val distanceToEntry = kotlin.math.abs(price - pot.entryPrice) / pip
                if (distanceToEntry <= 2.5) {
                    stillActive.add(
                        pot.copy(
                            state = SignalState.ACTIVE_SNIPER,
                            liveCurrentPrice = price,
                            floatingPips = 0.0,
                            floatingPnLUsd = 0.0,
                            timestamp = "Triggered Live Just Now"
                        )
                    )
                    continue
                }
            }
            remainingPotentials.add(pot)
        }

        if (newlyFailed.isNotEmpty()) {
            currentFailed.addAll(0, newlyFailed)
            _failedSignals.value = currentFailed
        }
        if (newlyCompleted.isNotEmpty()) {
            currentCompleted.addAll(0, newlyCompleted)
            _completedSignals.value = currentCompleted
            _journalMetrics.value = MarketDataProvider.calculateJournalMetrics().copy(
                totalSignals = _completedSignals.value.size,
                winCount = _completedSignals.value.count { it.state != SignalState.COMPLETED_SL && it.state != SignalState.FAILED }
            )
        }

        _activeSignals.value = stillActive
        _potentialSignals.value = remainingPotentials

        // Update selected signal if it's currently selected
        val sel = _selectedSignal.value
        if (sel != null) {
            val updatedSel = stillActive.find { it.id == sel.id }
                ?: newlyCompleted.find { it.id == sel.id }
                ?: newlyFailed.find { it.id == sel.id }
                ?: remainingPotentials.find { it.id == sel.id }
            if (updatedSel != null) {
                _selectedSignal.value = updatedSel
            }
        }
    }


    fun selectInstrument(instrument: MarketInstrument) {
        _selectedInstrument.value = instrument
        _selectedSignal.value = _activeSignals.value.find { it.instrumentSymbol == instrument.symbol }
            ?: _potentialSignals.value.find { it.instrumentSymbol == instrument.symbol }
            ?: _failedSignals.value.find { it.instrumentSymbol == instrument.symbol }
            ?: MarketDataProvider.noTradeSignals.find { it.instrumentSymbol == instrument.symbol }
            ?: TechnicalAnalysisEngine.generateSmcIctSignal(instrument, _candles.value)
        updateChartAndConfluence(instrument, _selectedTimeframe.value)
    }

    fun acknowledgeFailedSignal(signalId: String) {
        _failedSignals.value = _failedSignals.value.map {
            if (it.id == signalId) it.copy(isAcknowledged = true) else it
        }
    }

    fun acknowledgeAllFailedSignals() {
        _failedSignals.value = _failedSignals.value.map {
            it.copy(isAcknowledged = true)
        }
    }

    fun selectTimeframe(timeframe: Timeframe) {
        _selectedTimeframe.value = timeframe
        updateChartAndConfluence(_selectedInstrument.value, timeframe)
    }

    fun toggleChartDisplayMode() {
        _chartDisplayMode.value = if (_chartDisplayMode.value == ChartDisplayMode.TRADINGVIEW_ADVANCED) {
            ChartDisplayMode.NATIVE_SMC_CANVAS
        } else {
            ChartDisplayMode.TRADINGVIEW_ADVANCED
        }
    }

    private fun updateChartAndConfluence(instrument: MarketInstrument, timeframe: Timeframe) {
        val baseCandles = MarketDataProvider.generateCandles(
            symbol = instrument.symbol,
            timeframe = timeframe,
            basePrice = instrument.currentPrice,
            count = 45
        )
        _candles.value = baseCandles
        _confluenceMatrix.value = MarketDataProvider.getConfluenceMatrix(instrument.symbol)

        // For crypto, fetch authentic real-time Binance OHLC candles
        if (instrument.symbol == "BTCUSD") {
            viewModelScope.launch {
                val realCandles = MarketDataService.fetchRealCryptoCandles(interval = timeframe.tvInterval, limit = 45)
                if (realCandles.isNotEmpty()) {
                    _candles.value = realCandles
                }
            }
        }
    }

    /**
     * Executes an on-demand real-time live market scan using the Technical Analysis Engine.
     */
    fun triggerLiveScan() {
        viewModelScope.launch {
            _isScanningLive.value = true
            try {
                val (updatedInstruments, info) = MarketDataService.fetchLiveQuotes(_instruments.value)
                _instruments.value = updatedInstruments
                _dataProviderInfo.value = info

                // Generate fresh technical analysis signals on the actual live market data
                val refreshedActives = mutableListOf<SniperSignal>()
                for (inst in updatedInstruments.take(8)) {
                    val candles = MarketDataProvider.generateCandles(inst.symbol, Timeframe.H1, inst.currentPrice, 45)
                    val signal = TechnicalAnalysisEngine.generateSmcIctSignal(inst, candles)
                    if (signal.state == SignalState.CONFIRMED_SETUP || signal.state == SignalState.ACTIVE_SNIPER) {
                        refreshedActives.add(signal)
                    }
                }
                if (refreshedActives.isNotEmpty()) {
                    _activeSignals.value = refreshedActives
                }

                evaluateLiveSignals(updatedInstruments)
            } finally {
                _isScanningLive.value = false
            }
        }
    }

    /**
     * Tests and verifies an external institutional API connection (TwelveData, Finnhub, Alpha Vantage).
     */
    fun testExternalApi(provider: String, apiKey: String) {
        viewModelScope.launch {
            _customApiProvider.value = provider
            _customApiKey.value = apiKey
            val (success, message) = MarketDataService.testExternalApiConnection(provider, apiKey)
            _apiTestResult.value = if (success) "✓ $message" else "✗ $message"
            if (success) {
                _dataProviderInfo.value = _dataProviderInfo.value.copy(
                    providerName = "$provider Institutional Feed",
                    status = DataFeedStatus.LIVE_REAL_TIME,
                    primarySource = "$provider Direct API + TradingView",
                    lastUpdateTimestamp = System.currentTimeMillis()
                )
            }
        }
    }

    fun openChartForSignal(signal: SniperSignal) {
        val inst = _instruments.value.find { it.symbol == signal.instrumentSymbol }
            ?: _instruments.value.first()
        _selectedInstrument.value = inst
        _selectedSignal.value = signal
        _selectedTimeframe.value = Timeframe.M15
        updateChartAndConfluence(inst, Timeframe.M15)
        _currentScreen.value = AppScreen.LIVE_CHARTS
    }

    fun toggleWatchlist(symbol: String) {
        val current = _watchlistSymbols.value.toMutableSet()
        if (current.contains(symbol)) {
            current.remove(symbol)
        } else {
            current.add(symbol)
        }
        _watchlistSymbols.value = current
    }

    fun isWatchlist(symbol: String): Boolean {
        return _watchlistSymbols.value.contains(symbol)
    }

    fun setCategory(category: AssetCategory) {
        _selectedCategory.value = category
    }

    fun setSignalsTab(tab: SignalsTab) {
        _activeSignalsTab.value = tab
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openRiskCalculator(signal: SniperSignal?) {
        _riskDialogSignal.value = signal
    }

    fun closeRiskCalculator() {
        _riskDialogSignal.value = null
    }

    fun updateRiskSettings(equity: Double, riskPct: Double, dailyLossPct: Double) {
        _accountEquity.value = equity
        _riskPercentPerTrade.value = riskPct
        _dailyLossLimitPercent.value = dailyLossPct
    }
}

