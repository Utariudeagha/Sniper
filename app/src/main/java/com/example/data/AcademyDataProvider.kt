package com.example.data

import com.example.model.AcademyLesson
import com.example.model.DiagramType
import com.example.model.LessonCategory
import com.example.model.QuizQuestion

object AcademyDataProvider {

    val lessons = listOf(
        AcademyLesson(
            id = "lesson-01",
            title = "The Market Maker Playbook & Algorithmic Delivery",
            category = LessonCategory.MARKET_MAKER_SECRETS,
            readTimeMinutes = 8,
            summary = "Understanding IPDA (Interbank Price Delivery Algorithm) and why price moves to seek liquidity, not random technical lines.",
            fullContent = """
50 YEARS OF MARKET REALITY:
Retail traders believe that technical indicators like Moving Averages, RSI, or Stochastic drive price. They do not. Price in the global currency and commodities market is delivered by algorithmic systems (IPDA - Interbank Price Delivery Algorithm) managed by Tier-1 liquidity providers and central bank counter-parties.

THE CORE OBJECTIVE:
The market maker algorithm has only two functions:
1. Rebalance Fair Value (mitigate inefficiencies and Fair Value Gaps).
2. Purge Liquidity (hunt resting Buy-Stop and Sell-Stop orders).

THE 3-PHASE CYCLE:
1. Accumulation: Price is compressed inside a tight range (typically Asian session). Retail traders draw support and resistance boxes and place stops immediately above and below.
2. Manipulation (The Trap / Judas Swing): An explosive move in the OPPOSITE direction of the true trend. Its purpose is to trigger retail breakout entries and purge resting stops.
3. Distribution: The real institutional expansion into deep liquidity targets (TP1 1:3 and TP2 1:5).
            """.trimIndent(),
            institutionalSecrets = listOf(
                "Market makers never trade with market orders; they need massive pools of opposing limit orders to fill institutional volume.",
                "Equal Highs (EQH) and Equal Lows (EQL) are retail traps engineered purposefully as liquidity magnets.",
                "The London Open (07:00-09:00 UTC) and New York Open (12:30-14:30 UTC) are the primary killzones where manipulation peaks."
            ),
            goldenRules = listOf(
                "Never enter during the initial breakout of a session range.",
                "Wait for the liquidity sweep of the Asian range before seeking entry.",
                "Always align your lower-timeframe entry with the higher-timeframe order flow."
            ),
            visualDiagramType = DiagramType.JUDAS_SWING_DIAGRAM
        ),
        AcademyLesson(
            id = "lesson-02",
            title = "Liquidity Traps & The London Judas Swing",
            category = LessonCategory.LIQUIDITY_AND_TRAPS,
            readTimeMinutes = 7,
            summary = "How to spot and capitalize on stop hunts above previous session highs and lows instead of becoming the victim.",
            fullContent = """
WHAT IS LIQUIDITY?
In the institutional world, liquidity is simply orders.
- Buy-Side Liquidity (BSL): Resting buy-stop orders above swing highs, session highs, and retail resistance.
- Sell-Side Liquidity (SSL): Resting sell-stop orders below swing lows, session lows, and retail support.

THE JUDAS SWING MECHANISM:
Named after the false disciple, the Judas Swing is a deceptive run on price right around London Open (02:00-03:00 AM EST / 07:00-08:00 UTC).
If the higher timeframe daily bias is BULLISH:
- The algorithm will aggressively engineer a fast downward spike during London Open to sweep the Asian Session Low.
- Retail traders panic sell or trigger breakout sell-stops.
- Institutional algorithms absorb every single sell order at a discount price.
- Once sell liquidity is collected, price prints a swift 5M/15M Change of Character (CHoCH) and expands upward.
            """.trimIndent(),
            institutionalSecrets = listOf(
                "The fast wick outside a range is not weakness; it is smart money loading orders at wholesale prices.",
                "Retail breakout traders provide the exact exit liquidity market makers need.",
                "When you see clean 'double bottoms', understand that institutional computers see a dollar sign."
            ),
            goldenRules = listOf(
                "Identify Buy-Side and Sell-Side liquidity before marking any entry.",
                "The highest win-rate sniper entries happen AFTER a liquidity sweep has completed.",
                "If you cannot spot where the retail trap is, YOU are the trap."
            ),
            visualDiagramType = DiagramType.LIQUIDITY_SWEEP_DIAGRAM
        ),
        AcademyLesson(
            id = "lesson-03",
            title = "Order Blocks (OB) & Fair Value Gaps (FVG)",
            category = LessonCategory.ORDER_BLOCKS_AND_FVG,
            readTimeMinutes = 9,
            summary = "Identifying true institutional footprint candles and 3-bar imbalance gaps for precision sniper entries.",
            fullContent = """
DECODING THE ORDER BLOCK:
An Order Block is NOT simply the last opposite candle before an impulse. A high-probability institutional Order Block MUST satisfy three strict conditions:
1. It must sweep prior liquidity (swing high or low).
2. It must cause an aggressive Break of Structure (BOS) or Change of Character (CHoCH) with strong displacement.
3. It must leave an unmitigated Fair Value Gap (FVG) immediately ahead of it.

WHAT IS A FAIR VALUE GAP (FVG)?
An FVG occurs in a 3-candlestick sequence where Candle 1's high (or low) and Candle 3's low (or high) do not overlap. The empty space in Candle 2 is an imbalance where only one side of the market was delivered.

Price acts like a magnet to return and re-balance this gap (Fair Value rebalancing). When an FVG aligns with an unmitigated Order Block in a discount zone, you have an institutional sniper zone with 90%+ probability!
            """.trimIndent(),
            institutionalSecrets = listOf(
                "Displacement is the signature of institutional intervention. Tiny candles mean retail noise.",
                "Once an Order Block has been tapped once (mitigated), its probability drops significantly on subsequent touches.",
                "Inversion FVGs: An FVG that fails to hold becomes a powerful breaker zone in the opposite direction."
            ),
            goldenRules = listOf(
                "Only trade Order Blocks that created clear displacement and market structure shifts.",
                "Set your sniper entry at the beginning of the FVG or 50% equilibrium of the Order Block.",
                "Protect your capital by placing your stop loss safely beyond the invalidation wick."
            ),
            visualDiagramType = DiagramType.ORDER_BLOCK_DIAGRAM
        ),
        AcademyLesson(
            id = "lesson-04",
            title = "Top-Down Multi-Timeframe Confluence (The Master Skill)",
            category = LessonCategory.TOP_DOWN_MASTERY,
            readTimeMinutes = 10,
            summary = "The definitive pyramid: Aligning Monthly/Weekly narrative with Daily trend, 4H POIs, and 5M sniper executions.",
            fullContent = """
ALIGNMENT IS SUPREMACY:
"Alignment of LTF price delivery with HTF bias is the highest level of mastery there is."

When all timeframes agree, trade execution feels effortless. When timeframes conflict, chop and stop-outs are guaranteed.

THE 8-TIMEFRAME CONFLUENCE PYRAMID:
1. Macro Bias (Monthly & Weekly):
   - What is the macro direction of central bank order flow?
   - Where are the yearly/monthly liquidity targets resting?

2. Intermediate Structure (Daily & 4-Hour):
   - Daily defines the immediate trend and key swing points.
   - 4-Hour defines high-probability Order Blocks and Fair Value Gaps.

3. Tactical Setup (1-Hour & 30-Minute):
   - Maps session highs and lows (Asian, London, New York).
   - Identifies inducement zones and pending liquidity sweeps.

4. Sniper Execution (15-Minute & 5-Minute):
   - Price must tap the 4H/Daily POI.
   - 15M/5M must print a clear Change of Character (CHoCH) and displacement.
   - Enter on the retest of the LTF FVG with minimal risk and maximum reward (1:3 to 1:5).
            """.trimIndent(),
            institutionalSecrets = listOf(
                "A 5-minute trend against a Daily Order Block is destined to be crushed.",
                "Trading with HTF order flow allows you to absorb temporary drawdown and target massive 1:5+ R-multiples.",
                "If the Monthly, Weekly, and Daily timeframes disagree, STAY FLAT. Cash is a valid position."
            ),
            goldenRules = listOf(
                "Never buy at premium prices; only buy in discount zones (below 50% Fib equilibrium).",
                "Require at least 88% confluence across all 8 timeframes before risking capital.",
                "A sniper entry reduces stop loss size from 50 pips to 10-15 pips, multiplying returns."
            ),
            visualDiagramType = DiagramType.TOP_DOWN_PYRAMID
        ),
        AcademyLesson(
            id = "lesson-05",
            title = "50-Year Risk Management & Trading Psychology",
            category = LessonCategory.RISK_AND_DISCIPLINE,
            readTimeMinutes = 8,
            summary = "Why 95% of traders fail, the mathematics of asymmetric risk:reward, and psychological iron discipline.",
            fullContent = """
THE MATHEMATICS OF 50-YEAR SURVIVAL:
Amateur traders focus on "how much can I make on this trade?"
Institutional risk managers focus exclusively on "how much could I lose, and is my capital safe?"

THE 1-2% CAPITAL PRESERVATION LAW:
- Never risk more than 1% to 2% of total equity on any single idea.
- At 1% risk per trade, you can endure 10 consecutive losses and still have over 90% of your account intact.

THE 1:3 TO 1:5 ASYMMETRIC ADVANTAGE:
When you consistently demand a minimum of 1:3 to 1:5 Risk to Reward ratio:
- At 1:3 RR, a modest 40% win rate produces an astronomical profit curve.
- 10 trades: 4 wins at +3R = +12R. 6 losses at -1R = -6R. Net result = +6R (+6% to +12% gain).
- At 1:5 RR, even a 30% win rate yields +15R - 7R = +8R net profit!

THE THREE FATAL EMOTIONAL SINS:
1. Revenge Trading: Doubling lot size after a loss to "get even".
2. FOMO (Fear Of Missing Out): Entering mid-candle after a massive spike.
3. Moving the Stop Loss: Refusing to accept being wrong.
            """.trimIndent(),
            institutionalSecrets = listOf(
                "The market cannot defeat you if your position sizing is mathematically bulletproof.",
                "Professional traders have losing days and losing weeks; their edge is that winners are 3x to 5x their losers.",
                "Trading discipline is not an option; it is the sole differentiator between a gambler and an institutional operator."
            ),
            goldenRules = listOf(
                "Calculate your exact lot size before clicking buy or sell.",
                "Move stop loss to Breakeven (BE) immediately once Take Profit 1 (1:3) is secured.",
                "Enforce a maximum daily loss limit (e.g., -2%); if hit, close charts until next session."
            ),
            visualDiagramType = DiagramType.RISK_REWARD_LADDER
        )
    )

    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            question = "What is the primary objective of the Interbank Price Delivery Algorithm (IPDA)?",
            options = listOf(
                "To follow retail moving averages and RSI indicators",
                "To hunt resting liquidity pools and rebalance Fair Value Gaps",
                "To keep price moving in a perpetual horizontal range",
                "To trade exclusively based on news headlines"
            ),
            correctIndex = 1,
            explanation = "Institutional algorithms are programmed to rebalance market inefficiencies (FVGs) and seek concentrated liquidity pools (Buy & Sell stops) to fill large institutional orders."
        ),
        QuizQuestion(
            id = 2,
            question = "What makes the London Judas Swing so powerful for sniper traders?",
            options = listOf(
                "It represents the true trend that continues all day",
                "It occurs at midnight when markets are closed",
                "It is a deliberate false move that sweeps session liquidity before reversing into the true HTF trend",
                "It only works on equity indices"
            ),
            correctIndex = 2,
            explanation = "The Judas Swing traps early retail breakout traders and purges stops outside the Asian range, allowing smart money to enter at wholesale discount prices."
        ),
        QuizQuestion(
            id = 3,
            question = "Which requirement is CRITICAL for a valid high-probability Order Block?",
            options = listOf(
                "Must be a green candle on every single timeframe",
                "Must have purged liquidity, created displacement, and left a Fair Value Gap",
                "Must have been tested at least 5 times previously",
                "Must be accompanied by an overbought RSI signal"
            ),
            correctIndex = 1,
            explanation = "An authentic institutional Order Block must clear prior liquidity and cause a market structure shift (displacement) leaving an unmitigated FVG."
        ),
        QuizQuestion(
            id = 4,
            question = "If your account balance is $10,000 and you risk 1% with a 20-pip stop loss, what is your maximum allowed dollar risk?",
            options = listOf(
                "$200",
                "$50",
                "$100",
                "$500"
            ),
            correctIndex = 2,
            explanation = "1% of $10,000 = $100.00. This $100 risk divided by 20 pips ($5/pip) yields exactly 0.50 standard lots."
        ),
        QuizQuestion(
            id = 5,
            question = "Why does the SniperFX system strictly enforce 1:3 to 1:5 Risk to Reward targets?",
            options = listOf(
                "Because institutional edges rely on asymmetric payouts where winners dwarf losers",
                "Because smaller targets are illegal in forex",
                "Because market makers only trade once per month",
                "Because it guarantees 100% win rate on every single trade"
            ),
            correctIndex = 0,
            explanation = "With asymmetric 1:3 to 1:5 RR, even a modest 40% win rate produces phenomenal compounding, protecting capital against inevitable market fluctuations."
        )
    )
}
