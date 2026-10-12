package com.example.model

/**
 * Education curriculum module based on 50+ years of institutional trading secrets.
 */
data class AcademyLesson(
    val id: String,
    val title: String,
    val category: LessonCategory,
    val readTimeMinutes: Int,
    val summary: String,
    val fullContent: String,
    val institutionalSecrets: List<String>,
    val goldenRules: List<String>,
    val visualDiagramType: DiagramType
)

enum class LessonCategory(val label: String) {
    MARKET_MAKER_SECRETS("Market Maker Secrets"),
    LIQUIDITY_AND_TRAPS("Liquidity & Traps"),
    TOP_DOWN_MASTERY("Top-Down Confluence"),
    ORDER_BLOCKS_AND_FVG("Order Blocks & FVGs"),
    RISK_AND_DISCIPLINE("Risk & 50-Yr Discipline"),
    SNIPER_EXECUTION("Sniper Entry Execution")
}

enum class DiagramType {
    LIQUIDITY_SWEEP_DIAGRAM,
    JUDAS_SWING_DIAGRAM,
    ORDER_BLOCK_DIAGRAM,
    FAIR_VALUE_GAP_DIAGRAM,
    TOP_DOWN_PYRAMID,
    RISK_REWARD_LADDER
}

/**
 * Interactive trading psychology and mastery quiz question.
 */
data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
