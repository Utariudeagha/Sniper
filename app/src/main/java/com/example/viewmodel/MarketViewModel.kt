package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MarketDataProvider
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
    SIGNALS,
    CHART_DETAIL,
    WATCHLIST,
    ACADEMY,
    RISK_CALCULATOR
}

enum class SignalsTab {
    ACTIVE_SNIPER,
    POTENTIAL_SETUPS,
    COMPLETED_LEDGER
}

class MarketViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.SIGNALS)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeSignalsTab = MutableStateFlow(SignalsTab.ACTIVE_SNIPER)
    val activeSignalsTab: StateFlow<SignalsTab> = _activeSignalsTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AssetCategory.ALL)
    val selectedCategory: StateFlow<AssetCategory> = _selectedCategory.asStateFlow()

    private val _watchlistSymbols = MutableStateFlow(
        setOf("XAUUSD", "GBPUSD", "US100", "GBPJPY", "BTCUSD", "AUDUSD")
    )
    val watchlistSymbols: StateFlow<Set<String>> = _watchlistSymbols.asStateFlow()

    private val _instruments = MutableStateFlow(MarketDataProvider.defaultInstruments)
    val instruments: StateFlow<List<MarketInstrument>> = _instruments.asStateFlow()

    private val _selectedInstrument = MutableStateFlow(MarketDataProvider.defaultInstruments.first())
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

    private val _riskDialogSignal = MutableStateFlow<SniperSignal?>(null)
    val riskDialogSignal: StateFlow<SniperSignal?> = _riskDialogSignal.asStateFlow()

    init {
        updateChartAndConfluence(_selectedInstrument.value, _selectedTimeframe.value)
        startRealTimePriceFeed()
    }

    private fun startRealTimePriceFeed() {
        viewModelScope.launch {
            val random = Random(42)
            while (true) {
                delay(2000L) // Pulse every 2 seconds
                _instruments.value = _instruments.value.map { inst ->
                    val deltaRatio = (random.nextDouble() - 0.49) * 0.0006
                    val newPrice = inst.currentPrice * (1.0 + deltaRatio)
                    val halfSpread = (inst.spreadPips * inst.pipSize) / 2.0
                    inst.copy(
                        currentPrice = newPrice,
                        bidPrice = newPrice - halfSpread,
                        askPrice = newPrice + halfSpread,
                        change24hPercent = inst.change24hPercent + (deltaRatio * 10.0)
                    )
                }

                // Keep selectedInstrument updated
                val updatedCurrent = _instruments.value.find { it.symbol == _selectedInstrument.value.symbol }
                if (updatedCurrent != null) {
                    _selectedInstrument.value = updatedCurrent
                }
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

    private fun updateChartAndConfluence(instrument: MarketInstrument, timeframe: Timeframe) {
        _candles.value = MarketDataProvider.generateCandles(
            symbol = instrument.symbol,
            timeframe = timeframe,
            basePrice = instrument.currentPrice,
            count = 45
        )
        _confluenceMatrix.value = MarketDataProvider.getConfluenceMatrix(instrument.symbol)
    }

    fun openChartForSignal(signal: SniperSignal) {
        val inst = _instruments.value.find { it.symbol == signal.instrumentSymbol }
            ?: _instruments.value.first()
        _selectedInstrument.value = inst
        _selectedSignal.value = signal
        _selectedTimeframe.value = Timeframe.M15
        updateChartAndConfluence(inst, Timeframe.M15)
        _currentScreen.value = AppScreen.CHART_DETAIL
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
}
