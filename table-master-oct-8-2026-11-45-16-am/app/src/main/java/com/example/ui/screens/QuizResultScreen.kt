package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.QuizViewModel
import com.example.ui.components.StatCard
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.MathTertiary
import com.example.ui.theme.WrongRed

@Composable
fun QuizResultScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val result by viewModel.lastResult.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                Text("Back to Home")
            }
        }
        return
    }

    val res = result!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("result_screen"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Celebration Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val badgeEmoji = when {
                        res.accuracy >= 90f -> "🏆"
                        res.accuracy >= 70f -> "🌟"
                        res.accuracy >= 50f -> "👍"
                        else -> "💪"
                    }
                    val feedbackTitle = when {
                        res.accuracy >= 90f -> "Outstanding Mastery!"
                        res.accuracy >= 70f -> "Great Practice Session!"
                        res.accuracy >= 50f -> "Good Effort! Keep Going!"
                        else -> "Practice Makes Perfect!"
                    }

                    Text(badgeEmoji, fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = feedbackTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${res.category.displayName} • ${res.difficulty.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${res.score} PTS",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Stats Matrix
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Accuracy",
                    value = "${res.accuracy.toInt()}%",
                    icon = Icons.Default.Check,
                    iconTint = CorrectGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Max Streak",
                    value = "${res.maxStreak} 🔥",
                    icon = Icons.Default.Whatshot,
                    iconTint = MathTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Correct / Total",
                    value = "${res.correctCount}/${res.totalQuestions}",
                    icon = Icons.Default.Check,
                    iconTint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Time Taken",
                    value = "${res.durationSeconds}s",
                    icon = Icons.Default.Timer,
                    iconTint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Buttons Row
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.startQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("play_again_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY AGAIN", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.SCOREBOARD) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("go_to_scoreboard_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scoreboard", fontWeight = FontWeight.Medium)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("go_to_home_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Home", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Breakdown Review Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Question Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Individual Question Breakdown items
        itemsIndexed(res.answerRecords) { index, record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (record.isCorrect)
                        CorrectGreen.copy(alpha = 0.08f)
                    else WrongRed.copy(alpha = 0.08f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (record.isCorrect) CorrectGreen.copy(alpha = 0.4f) else WrongRed.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (record.isCorrect) CorrectGreen else WrongRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (record.isCorrect) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (record.isCorrect) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "${index + 1}. ${record.question.prompt}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (record.isCorrect) {
                                Text(
                                    text = "Answer: ${record.question.correctAnswer}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CorrectGreen
                                )
                            } else {
                                Text(
                                    text = "Your: ${record.selectedAnswer} • Correct: ${record.question.correctAnswer}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WrongRed
                                )
                            }
                        }
                    }

                    Text(
                        text = "${record.timeTakenMillis / 1000f}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
