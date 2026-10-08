package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MistakeEntity
import com.example.data.local.QuizSessionEntity
import com.example.data.model.AnswerRecord
import com.example.data.model.Question
import com.example.data.model.QuizCategory
import com.example.data.model.QuizConfig
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizResult
import com.example.data.repository.QuizRepository
import com.example.quiz.QuestionGenerator
import com.example.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    QUIZ,
    RESULT,
    SCOREBOARD,
    MISTAKES,
    STUDY_CHARTS
}

enum class ThemeMode(val title: String) {
    SYSTEM("Follow System"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

data class ActiveQuizState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val isAnswerChecked: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val answerRecords: List<AnswerRecord> = emptyList(),
    val isContinuousMode: Boolean = false,
    val isMistakePractice: Boolean = false,
    val sessionStartTime: Long = System.currentTimeMillis(),
    val questionStartTime: Long = System.currentTimeMillis()
) {
    val currentQuestion: Question?
        get() = if (currentIndex in questions.indices) questions[currentIndex] else null

    val totalQuestions: Int
        get() = questions.size

    val isLastQuestion: Boolean
        get() = !isContinuousMode && currentIndex >= questions.size - 1
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository = QuizRepository(
        AppDatabase.getDatabase(application).quizDao()
    )

    val soundManager = SoundManager(viewModelScope)

    // Settings state
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isSfxEnabled = MutableStateFlow(true)
    val isSfxEnabled: StateFlow<Boolean> = _isSfxEnabled.asStateFlow()

    private val _isMusicEnabled = MutableStateFlow(false)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _config = MutableStateFlow(QuizConfig())
    val config: StateFlow<QuizConfig> = _config.asStateFlow()

    private val _activeQuizState = MutableStateFlow(ActiveQuizState())
    val activeQuizState: StateFlow<ActiveQuizState> = _activeQuizState.asStateFlow()

    private val _lastResult = MutableStateFlow<QuizResult?>(null)
    val lastResult: StateFlow<QuizResult?> = _lastResult.asStateFlow()

    // Database statistics and records
    val allSessions: StateFlow<List<QuizSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMistakes: StateFlow<List<MistakeEntity>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSessionsCount: StateFlow<Int> = repository.totalSessionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalQuestionsAnswered: StateFlow<Int?> = repository.totalQuestionsAnswered
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCorrectAnswers: StateFlow<Int?> = repository.totalCorrectAnswers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val highScore: StateFlow<Int?> = repository.highScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestStreak: StateFlow<Int?> = repository.bestStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        soundManager.isSfxEnabled = _isSfxEnabled.value
        soundManager.isMusicEnabled = _isMusicEnabled.value
    }

    fun openSettings() {
        _isSettingsOpen.value = true
        soundManager.playClickSound()
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        soundManager.playClickSound()
    }

    fun toggleSfx(enabled: Boolean) {
        _isSfxEnabled.value = enabled
        soundManager.isSfxEnabled = enabled
        if (enabled) {
            soundManager.playClickSound()
        }
    }

    fun toggleMusic(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        soundManager.isMusicEnabled = enabled
    }

    fun playClickSound() {
        soundManager.playClickSound()
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playClickSound()
        _currentScreen.value = screen
    }

    // Config adjustments
    fun updateCategory(category: QuizCategory) {
        soundManager.playClickSound()
        _config.value = _config.value.copy(category = category)
    }

    fun updateDifficulty(difficulty: QuizDifficulty) {
        soundManager.playClickSound()
        if (difficulty == QuizDifficulty.CUSTOM) {
            _config.value = _config.value.copy(difficulty = difficulty)
        } else {
            _config.value = QuizConfig.forPreset(difficulty, _config.value.category)
        }
    }

    fun updateTableRange(min: Int, max: Int) {
        _config.value = _config.value.copy(
            difficulty = QuizDifficulty.CUSTOM,
            tableMin = min.coerceIn(1, 50),
            tableMax = max.coerceIn(min, 50)
        )
    }

    fun updateMultiplierRange(min: Int, max: Int) {
        _config.value = _config.value.copy(
            difficulty = QuizDifficulty.CUSTOM,
            tableMultiplierMin = min.coerceIn(1, 25),
            tableMultiplierMax = max.coerceIn(min, 25)
        )
    }

    fun updateSquareRange(min: Int, max: Int) {
        _config.value = _config.value.copy(
            difficulty = QuizDifficulty.CUSTOM,
            squareMin = min.coerceIn(1, 50),
            squareMax = max.coerceIn(min, 50)
        )
    }

    fun updateCubeRange(min: Int, max: Int) {
        _config.value = _config.value.copy(
            difficulty = QuizDifficulty.CUSTOM,
            cubeMin = min.coerceIn(1, 30),
            cubeMax = max.coerceIn(min, 30)
        )
    }

    fun toggleRandomizeUnpredictable(enabled: Boolean) {
        soundManager.playClickSound()
        _config.value = _config.value.copy(randomizeUnpredictable = enabled)
    }

    fun updateQuestionCount(count: Int) {
        soundManager.playClickSound()
        _config.value = _config.value.copy(questionCount = count)
    }

    // Start a quiz with current config
    fun startQuiz() {
        soundManager.playClickSound()
        val currentCfg = _config.value
        val questions = QuestionGenerator.generateQuestions(
            config = currentCfg,
            count = if (currentCfg.questionCount == 0) 10 else currentCfg.questionCount
        )
        _activeQuizState.value = ActiveQuizState(
            questions = questions,
            currentIndex = 0,
            isContinuousMode = currentCfg.questionCount == 0,
            isMistakePractice = false,
            sessionStartTime = System.currentTimeMillis(),
            questionStartTime = System.currentTimeMillis()
        )
        _currentScreen.value = AppScreen.QUIZ
    }

    // Start a quiz focused on previously made mistakes
    fun startMistakePractice() {
        soundManager.playClickSound()
        val mistakes = allMistakes.value
        if (mistakes.isEmpty()) return

        val questions = mistakes.map { m ->
            val distractors = mutableSetOf(m.correctAnswer)
            val offsets = listOf(-10, 10, -1, 1, -2, 2, -5, 5, -20, 20)
            for (off in offsets) {
                val cand = m.correctAnswer + off
                if (cand > 0 && cand != m.correctAnswer) {
                    distractors.add(cand)
                    if (distractors.size == 4) break
                }
            }
            while (distractors.size < 4) {
                distractors.add(m.correctAnswer + distractors.size * 3)
            }

            Question(
                prompt = m.questionPrompt,
                categoryTag = "Mistake Review",
                correctAnswer = m.correctAnswer,
                options = distractors.shuffled(),
                explanation = m.explanation,
                category = QuizCategory.entries.find { it.name == m.categoryName } ?: QuizCategory.TABLES
            )
        }

        _activeQuizState.value = ActiveQuizState(
            questions = questions,
            currentIndex = 0,
            isContinuousMode = false,
            isMistakePractice = true,
            sessionStartTime = System.currentTimeMillis(),
            questionStartTime = System.currentTimeMillis()
        )
        _currentScreen.value = AppScreen.QUIZ
    }

    // When user selects an option
    fun selectAnswer(selected: Int) {
        val state = _activeQuizState.value
        if (state.isAnswerChecked) return // Prevent multiple selections
        val q = state.currentQuestion ?: return

        val isCorrect = (selected == q.correctAnswer)
        val timeTaken = System.currentTimeMillis() - state.questionStartTime

        if (isCorrect) {
            soundManager.playCorrectSound()
        } else {
            soundManager.playWrongSound()
        }

        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newMaxStreak = maxOf(state.maxStreak, newStreak)
        val streakBonus = if (isCorrect) minOf(state.currentStreak * 2, 20) else 0
        val pointsEarned = if (isCorrect) 10 + streakBonus else 0
        val newScore = state.score + pointsEarned

        val record = AnswerRecord(
            question = q,
            selectedAnswer = selected,
            isCorrect = isCorrect,
            timeTakenMillis = timeTaken
        )

        _activeQuizState.value = state.copy(
            selectedOption = selected,
            isAnswerChecked = true,
            isCorrect = isCorrect,
            score = newScore,
            currentStreak = newStreak,
            maxStreak = newMaxStreak,
            answerRecords = state.answerRecords + record
        )

        viewModelScope.launch {
            if (!isCorrect) {
                repository.recordMistake(
                    prompt = q.prompt,
                    correctAnswer = q.correctAnswer,
                    userAnswer = selected,
                    explanation = q.explanation,
                    categoryName = q.category.name
                )
            } else if (state.isMistakePractice) {
                repository.resolveMistake(q.prompt)
            }
        }
    }

    // Move to next question or complete quiz
    fun nextQuestion() {
        val state = _activeQuizState.value
        if (!state.isAnswerChecked) return
        soundManager.playClickSound()

        if (state.isContinuousMode) {
            val newQ = QuestionGenerator.generateSingleQuestion(_config.value)
            val updatedQuestions = state.questions + newQ
            _activeQuizState.value = state.copy(
                questions = updatedQuestions,
                currentIndex = state.currentIndex + 1,
                selectedOption = null,
                isAnswerChecked = false,
                isCorrect = false,
                questionStartTime = System.currentTimeMillis()
            )
        } else if (state.currentIndex < state.questions.size - 1) {
            _activeQuizState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedOption = null,
                isAnswerChecked = false,
                isCorrect = false,
                questionStartTime = System.currentTimeMillis()
            )
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val state = _activeQuizState.value
        val totalQ = state.answerRecords.size
        val correctCount = state.answerRecords.count { it.isCorrect }
        val wrongCount = totalQ - correctCount
        val accuracy = if (totalQ > 0) (correctCount.toFloat() / totalQ) * 100f else 0f
        val durationSeconds = (System.currentTimeMillis() - state.sessionStartTime) / 1000

        val result = QuizResult(
            totalQuestions = totalQ,
            correctCount = correctCount,
            wrongCount = wrongCount,
            accuracy = accuracy,
            score = state.score,
            maxStreak = state.maxStreak,
            durationSeconds = durationSeconds,
            category = _config.value.category,
            difficulty = _config.value.difficulty,
            answerRecords = state.answerRecords
        )

        _lastResult.value = result

        viewModelScope.launch {
            repository.saveSession(
                QuizSessionEntity(
                    categoryName = result.category.displayName,
                    difficultyName = result.difficulty.displayName,
                    totalQuestions = result.totalQuestions,
                    correctCount = result.correctCount,
                    score = result.score,
                    maxStreak = result.maxStreak,
                    durationSeconds = result.durationSeconds
                )
            )
        }

        _currentScreen.value = AppScreen.RESULT
    }

    fun clearAllHistory() {
        soundManager.playClickSound()
        viewModelScope.launch {
            repository.clearAllSessions()
        }
    }

    fun clearAllMistakes() {
        soundManager.playClickSound()
        viewModelScope.launch {
            repository.clearAllMistakes()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
