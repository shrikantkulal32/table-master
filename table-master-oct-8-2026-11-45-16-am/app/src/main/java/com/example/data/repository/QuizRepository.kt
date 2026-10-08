package com.example.data.repository

import com.example.data.local.MistakeEntity
import com.example.data.local.QuizDao
import com.example.data.local.QuizSessionEntity
import kotlinx.coroutines.flow.Flow

class QuizRepository(private val quizDao: QuizDao) {

    val allSessions: Flow<List<QuizSessionEntity>> = quizDao.getAllSessions()
    val allMistakes: Flow<List<MistakeEntity>> = quizDao.getAllMistakes()
    val totalSessionsCount: Flow<Int> = quizDao.getTotalSessionsCount()
    val totalQuestionsAnswered: Flow<Int?> = quizDao.getTotalQuestionsAnswered()
    val totalCorrectAnswers: Flow<Int?> = quizDao.getTotalCorrectAnswers()
    val highScore: Flow<Int?> = quizDao.getHighScore()
    val bestStreak: Flow<Int?> = quizDao.getBestStreak()

    suspend fun saveSession(session: QuizSessionEntity): Long {
        return quizDao.insertSession(session)
    }

    suspend fun recordMistake(
        prompt: String,
        correctAnswer: Int,
        userAnswer: Int,
        explanation: String,
        categoryName: String
    ) {
        val existing = quizDao.getMistakeByPrompt(prompt)
        val count = if (existing != null) existing.missCount + 1 else 1
        val entity = MistakeEntity(
            questionPrompt = prompt,
            correctAnswer = correctAnswer,
            lastWrongAnswer = userAnswer,
            explanation = explanation,
            categoryName = categoryName,
            missCount = count,
            lastMissedTimestamp = System.currentTimeMillis()
        )
        quizDao.insertOrUpdateMistake(entity)
    }

    suspend fun resolveMistake(prompt: String) {
        quizDao.deleteMistake(prompt)
    }

    suspend fun clearAllMistakes() {
        quizDao.clearAllMistakes()
    }

    suspend fun clearAllSessions() {
        quizDao.clearAllSessions()
    }
}
