package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.QuizViewModel
import com.example.ui.components.QuizOptionCard
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.MathTertiary
import com.example.ui.theme.WrongRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.activeQuizState.collectAsStateWithLifecycle()
    val isMusicEnabled by viewModel.isMusicEnabled.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val isSfxEnabled by viewModel.isSfxEnabled.collectAsStateWithLifecycle()

    val question = state.currentQuestion
    var showQuitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showQuitDialog = true
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quit Practice Round?") },
            text = { Text("Are you sure you want to exit? Your answered questions will be saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        showQuitDialog = false
                        viewModel.navigateTo(AppScreen.HOME)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Exit to Home")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("Resume")
                }
            }
        )
    }

    if (isSettingsOpen) {
        SettingsDialog(
            currentTheme = currentTheme,
            isSfxEnabled = isSfxEnabled,
            isMusicEnabled = isMusicEnabled,
            onThemeChange = { viewModel.setThemeMode(it) },
            onSfxToggle = { viewModel.toggleSfx(it) },
            onMusicToggle = { viewModel.toggleMusic(it) },
            onDismiss = { viewModel.closeSettings() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val progressTitle = if (state.isContinuousMode) {
                            "Question #${state.currentIndex + 1} (Continuous)"
                        } else {
                            "Question ${state.currentIndex + 1} of ${state.totalQuestions}"
                        }
                        Text(
                            text = progressTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = question?.categoryTag ?: "Math Challenge",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showQuitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit Quiz"
                        )
                    }
                },
                actions = {
                    // Quick Music Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleMusic(!isMusicEnabled) },
                        modifier = Modifier.testTag("quiz_music_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = if (isMusicEnabled) "Music ON" else "Music OFF",
                            tint = if (isMusicEnabled) MathTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.currentStreak > 0) MathTertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Streak",
                                tint = if (state.currentStreak > 0) MathTertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.currentStreak}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (state.currentStreak > 0) MathTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Score Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${state.score} pts",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Settings Button
                    IconButton(onClick = { viewModel.openSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("quiz_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Linear Progress Bar
            val progressFraction = if (state.isContinuousMode) {
                1f
            } else if (state.totalQuestions > 0) {
                (state.currentIndex + 1).toFloat() / state.totalQuestions.toFloat()
            } else 0f

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (question != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("question_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Category Tag badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (question.isUnpredictable)
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (question.isUnpredictable)
                                        "⚡ CHALLENGE: ${question.categoryTag.uppercase()}"
                                    else question.categoryTag.uppercase(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (question.isUnpredictable)
                                        MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Large mathematical prompt
                            Text(
                                text = question.prompt,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("question_prompt_text")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Select the correct answer below",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Multiple Choice Options (4 Choices)
                    val optionLabels = listOf("A", "B", "C", "D")
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        question.options.forEachIndexed { index, optionVal ->
                            val label = optionLabels.getOrElse(index) { "${index + 1}" }
                            val isSelected = state.selectedOption == optionVal
                            val isCorrect = optionVal == question.correctAnswer

                            QuizOptionCard(
                                optionNumber = label,
                                answerValue = optionVal,
                                isSelected = isSelected,
                                isCorrectOption = isCorrect,
                                isAnswerChecked = state.isAnswerChecked,
                                onClick = {
                                    viewModel.selectAnswer(optionVal)
                                }
                            )
                        }
                    }

                    // Feedback and Explanation banner
                    // Fix glitch: Use ExitTransition.None so it never animates out displaying red,
                    // and tie correctness directly to question & selection so right answers never flash red!
                    if (state.isAnswerChecked) {
                        val isCorrectAnswer = state.isCorrect

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feedback_banner"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrectAnswer)
                                    CorrectGreen.copy(alpha = 0.15f)
                                else WrongRed.copy(alpha = 0.15f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isCorrectAnswer) CorrectGreen else WrongRed
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCorrectAnswer) Icons.Default.CheckCircle else Icons.Default.Error,
                                        contentDescription = null,
                                        tint = if (isCorrectAnswer) CorrectGreen else WrongRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (isCorrectAnswer) "Right Answer! Well done." else "Wrong Answer! Correct answer is highlighted in green.",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrectAnswer) CorrectGreen else WrongRed
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Explanation: ${question.explanation}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Next Question Button
                    if (state.isAnswerChecked) {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("next_question_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = if (state.isLastQuestion) "VIEW FINAL SCOREBOARD ➔" else "NEXT QUESTION ➔",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
