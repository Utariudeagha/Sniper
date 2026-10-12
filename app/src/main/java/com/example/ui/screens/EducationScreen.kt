package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AcademyDataProvider
import com.example.model.AcademyLesson
import com.example.model.QuizQuestion
import com.example.ui.components.AcademyDiagramView
import com.example.ui.theme.*

@Composable
fun EducationScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Lessons, 1 = Institutional Quiz
    var expandedLessonId by remember { mutableStateOf<String?>("lesson-01") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Academy Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("academy_header_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        color = GoldPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Text(
                            text = "50 YEARS OF MARKET WISDOM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Institutional Trading Academy",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )

                    Text(
                        text = "Master Market Maker secrets, liquidity engineering, Order Blocks, and multi-timeframe top-down confluence. Unbeatable price action education.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Segmented Tab: Lessons vs Interactive Quiz
        item {
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val tabs = listOf("Masterclass Lessons", "Institutional Edge Quiz")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else androidx.compose.ui.graphics.Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edu_tab_$index")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ObsidianBackground else TextWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Masterclass Lessons List
            items(AcademyDataProvider.lessons, key = { it.id }) { lesson ->
                val isExpanded = expandedLessonId == lesson.id

                LessonAccordionCard(
                    lesson = lesson,
                    isExpanded = isExpanded,
                    onToggle = {
                        expandedLessonId = if (isExpanded) null else lesson.id
                    }
                )
            }
        } else {
            // Interactive Institutional Knowledge Check & Quiz
            item {
                QuizSection()
            }
        }
    }
}

@Composable
fun LessonAccordionCard(
    lesson: AcademyLesson,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isExpanded) CyanAccent.copy(alpha = 0.6f) else ObsidianCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() }
            .testTag("lesson_${lesson.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = CyanDark.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = lesson.category.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = lesson.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Text(
                        text = "${lesson.readTimeMinutes} min read • ${lesson.summary}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = if (isExpanded) CyanLight else TextMuted
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Divider(color = ObsidianCardBorder, modifier = Modifier.padding(bottom = 12.dp))

                    // Visual Diagram
                    AcademyDiagramView(diagramType = lesson.visualDiagramType)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Full Content
                    Text(
                        text = lesson.fullContent,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Institutional Secrets Box
                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "50-YEAR INSTITUTIONAL SECRETS:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            lesson.institutionalSecrets.forEach { secret ->
                                Text(
                                    text = "• $secret",
                                    fontSize = 11.sp,
                                    color = TextWhite,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Golden Rules Box
                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = BullishGreenLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "DISCIPLINE & EXECUTION RULES:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreenLight
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            lesson.goldenRules.forEach { rule ->
                                Text(
                                    text = "✓ $rule",
                                    fontSize = 11.sp,
                                    color = TextWhite,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizSection() {
    val questions = AcademyDataProvider.quizQuestions
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var showResults by remember { mutableStateOf(false) }

    val correctCount = remember(showResults, userAnswers) {
        questions.count { q -> userAnswers[q.id] == q.correctIndex }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        questions.forEachIndexed { index, question ->
            val selectedOption = userAnswers[question.id]
            val hasAnswered = selectedOption != null

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                    .testTag("quiz_card_${question.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUESTION ${index + 1} OF ${questions.size}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = question.question,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    question.options.forEachIndexed { optIndex, optionText ->
                        val isChosen = selectedOption == optIndex
                        val isCorrect = optIndex == question.correctIndex

                        val buttonBg = when {
                            !showResults && isChosen -> CyanDark
                            showResults && isCorrect -> BullishGreen
                            showResults && isChosen && !isCorrect -> BearishRed
                            else -> ObsidianSurface
                        }

                        Surface(
                            onClick = {
                                if (!showResults) {
                                    userAnswers[question.id] = optIndex
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = buttonBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isChosen) CyanAccent else ObsidianCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = ObsidianBackground.copy(alpha = 0.5f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                    }
                                }
                                Text(
                                    text = optionText,
                                    fontSize = 12.sp,
                                    color = TextWhite,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (showResults) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = ObsidianSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Institutional Explanation: ${question.explanation}",
                                fontSize = 11.sp,
                                color = CyanLight,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Submit Button or Score Card
        if (!showResults) {
            Button(
                onClick = { showResults = true },
                enabled = userAnswers.size == questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_quiz_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text(
                    text = if (userAnswers.size == questions.size) "Submit & Evaluate Edge" else "Answer All ${questions.size} Questions (${userAnswers.size}/${questions.size})",
                    color = ObsidianBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BullishGreen, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MASTERY SCORE: $correctCount / ${questions.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight
                    )
                    Text(
                        text = if (correctCount >= 4) "🏆 Institutional Edge Confirmed! You think like a market maker." else "Study the lessons to sharpen your discipline and liquidity awareness.",
                        fontSize = 12.sp,
                        color = TextWhite,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            userAnswers.clear()
                            showResults = false
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanLight)
                    ) {
                        Text("Retake Knowledge Check")
                    }
                }
            }
        }
    }
}
