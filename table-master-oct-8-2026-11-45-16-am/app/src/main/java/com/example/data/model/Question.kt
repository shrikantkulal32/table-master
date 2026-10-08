package com.example.data.model

data class Question(
    val id: String = java.util.UUID.randomUUID().toString(),
    val prompt: String,
    val categoryTag: String,
    val correctAnswer: Int,
    val options: List<Int>,
    val explanation: String,
    val category: QuizCategory,
    val isUnpredictable: Boolean = false
)

data class AnswerRecord(
    val question: Question,
    val selectedAnswer: Int,
    val isCorrect: Boolean,
    val timeTakenMillis: Long
)

data class QuizResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val accuracy: Float,
    val score: Int,
    val maxStreak: Int,
    val durationSeconds: Long,
    val category: QuizCategory,
    val difficulty: QuizDifficulty,
    val answerRecords: List<AnswerRecord>
)
