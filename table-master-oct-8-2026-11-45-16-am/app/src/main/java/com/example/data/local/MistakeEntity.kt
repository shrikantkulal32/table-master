package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey
    val questionPrompt: String,
    val correctAnswer: Int,
    val lastWrongAnswer: Int,
    val explanation: String,
    val categoryName: String,
    val missCount: Int = 1,
    val lastMissedTimestamp: Long = System.currentTimeMillis()
)
