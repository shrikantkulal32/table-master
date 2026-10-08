package com.example

import com.example.data.model.QuizCategory
import com.example.data.model.QuizConfig
import com.example.data.model.QuizDifficulty
import com.example.quiz.QuestionGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testTableQuestionsWithinCriteria() {
        val config = QuizConfig(
            category = QuizCategory.TABLES,
            tableMin = 1,
            tableMax = 20,
            tableMultiplierMin = 1,
            tableMultiplierMax = 10,
            randomizeUnpredictable = false,
            questionCount = 20
        )
        val questions = QuestionGenerator.generateQuestions(config, 20)
        assertEquals(20, questions.size)

        for (q in questions) {
            assertEquals(4, q.options.size)
            // Options must be all unique
            assertEquals(4, q.options.toSet().size)
            // Correct answer must be among options
            assertTrue(q.options.contains(q.correctAnswer))
            assertTrue(q.correctAnswer > 0)
        }
    }

    @Test
    fun testSquareQuestionsCriteria() {
        val config = QuizConfig(
            category = QuizCategory.SQUARES,
            squareMin = 1,
            squareMax = 20,
            randomizeUnpredictable = false,
            questionCount = 15
        )
        val questions = QuestionGenerator.generateQuestions(config, 15)
        assertEquals(15, questions.size)

        for (q in questions) {
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.toSet().size)
            assertTrue(q.options.contains(q.correctAnswer))
        }
    }

    @Test
    fun testCubeQuestionsCriteria() {
        val config = QuizConfig(
            category = QuizCategory.CUBES,
            cubeMin = 1,
            cubeMax = 15,
            randomizeUnpredictable = false,
            questionCount = 10
        )
        val questions = QuestionGenerator.generateQuestions(config, 10)
        assertEquals(10, questions.size)

        for (q in questions) {
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.toSet().size)
            assertTrue(q.options.contains(q.correctAnswer))
        }
    }

    @Test
    fun testRandomizeUnpredictableMode() {
        val config = QuizConfig(
            category = QuizCategory.RANDOM_CHALLENGE,
            tableMin = 1,
            tableMax = 20,
            squareMin = 1,
            squareMax = 20,
            cubeMin = 1,
            cubeMax = 15,
            randomizeUnpredictable = true,
            questionCount = 30
        )
        val questions = QuestionGenerator.generateQuestions(config, 30)
        assertEquals(30, questions.size)

        for (q in questions) {
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.toSet().size)
            assertTrue(q.options.contains(q.correctAnswer))
            assertTrue(q.correctAnswer > 0)
        }
    }

    @Test
    fun testPresets() {
        val easy = QuizConfig.forPreset(QuizDifficulty.EASY, QuizCategory.TABLES)
        assertEquals(10, easy.tableMax)
        assertEquals(10, easy.squareMax)
        assertEquals(5, easy.cubeMax)

        val medium = QuizConfig.forPreset(QuizDifficulty.MEDIUM, QuizCategory.TABLES)
        assertEquals(20, medium.tableMax)
        assertEquals(20, medium.squareMax)
        assertEquals(15, medium.cubeMax)
    }

    @Test
    fun testThemeModes() {
        assertEquals(3, com.example.ui.ThemeMode.entries.size)
        assertTrue(com.example.ui.ThemeMode.entries.contains(com.example.ui.ThemeMode.LIGHT))
        assertTrue(com.example.ui.ThemeMode.entries.contains(com.example.ui.ThemeMode.DARK))
        assertTrue(com.example.ui.ThemeMode.entries.contains(com.example.ui.ThemeMode.SYSTEM))
    }
}
