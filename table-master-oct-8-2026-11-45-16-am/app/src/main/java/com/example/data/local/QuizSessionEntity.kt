package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_sessions")
data class QuizSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val categoryName: String,
    val difficultyName: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val score: Int,
    val maxStreak: Int,
    val durationSeconds: Long
)
