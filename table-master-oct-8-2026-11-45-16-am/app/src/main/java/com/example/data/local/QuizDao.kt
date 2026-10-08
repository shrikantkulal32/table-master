package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    // Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: QuizSessionEntity): Long

    @Query("SELECT * FROM quiz_sessions ORDER BY timestamp DESC LIMIT 50")
    fun getAllSessions(): Flow<List<QuizSessionEntity>>

    @Query("SELECT COUNT(*) FROM quiz_sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT SUM(totalQuestions) FROM quiz_sessions")
    fun getTotalQuestionsAnswered(): Flow<Int?>

    @Query("SELECT SUM(correctCount) FROM quiz_sessions")
    fun getTotalCorrectAnswers(): Flow<Int?>

    @Query("SELECT MAX(score) FROM quiz_sessions")
    fun getHighScore(): Flow<Int?>

    @Query("SELECT MAX(maxStreak) FROM quiz_sessions")
    fun getBestStreak(): Flow<Int?>

    @Query("DELETE FROM quiz_sessions")
    suspend fun clearAllSessions()

    // Mistakes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMistake(mistake: MistakeEntity)

    @Query("SELECT * FROM mistakes WHERE questionPrompt = :prompt LIMIT 1")
    suspend fun getMistakeByPrompt(prompt: String): MistakeEntity?

    @Query("SELECT * FROM mistakes ORDER BY missCount DESC, lastMissedTimestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("DELETE FROM mistakes WHERE questionPrompt = :prompt")
    suspend fun deleteMistake(prompt: String)

    @Query("DELETE FROM mistakes")
    suspend fun clearAllMistakes()
}
