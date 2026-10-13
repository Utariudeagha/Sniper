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

    private val _completedSignals = MutableStateFlow(MarketDataProvider.completedSignals)
    val completedSignals: StateFlow<List<SniperSignal>> = _completedSignals.asStateFlow()

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

    init {
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
                } catch (e: Exception) {
                    _dataProviderInfo.value = _dataProviderInfo.value.copy(
                        status = DataFeedStatus.INDICATIVE_REAL_TIME
                    )
                }
                delay(3000L) // Polling interval
            }
        }
    }

    fun selectInstrument(instrument: MarketInstrument) {
        _selectedInstrument.value = instrument
        _selectedSignal.value = _activeSignals.value.find { it.instrumentSymbol == instrument.symbol }
            ?: _potentialSignals.value.find { it.instrumentSymbol == instrument.symbol }
        updateChartAndConfluence(instrument, _selectedTimeframe.value)
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
        val newCandles = MarketDataProvider.generateCandles(
            symbol = instrument.symbol,
            timeframe = timeframe,
            basePrice = instrument.currentPrice,
            count = 45
        )
        _candles.value = newCandles
        _confluenceMatrix.value = MarketDataProvider.getConfluenceMatrix(instrument.symbol)
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
