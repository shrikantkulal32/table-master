package com.example.data.model

data class QuizConfig(
    val category: QuizCategory = QuizCategory.TABLES,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM,
    // Tables criteria
    val tableMin: Int = 1,
    val tableMax: Int = 20,
    val tableMultiplierMin: Int = 1,
    val tableMultiplierMax: Int = 10,
    // Squares criteria
    val squareMin: Int = 1,
    val squareMax: Int = 20,
    // Cubes criteria
    val cubeMin: Int = 1,
    val cubeMax: Int = 15,
    // Randomize option: introduces unexpected & challenging questions (e.g. 14 × 17, missing factors, reverse powers)
    val randomizeUnpredictable: Boolean = true,
    // Number of questions per quiz (10, 20, 30, or 0 for continuous practice)
    val questionCount: Int = 15
) {
    companion object {
        fun forPreset(difficulty: QuizDifficulty, currentCategory: QuizCategory): QuizConfig {
            return when (difficulty) {
                QuizDifficulty.EASY -> QuizConfig(
                    category = currentCategory,
                    difficulty = QuizDifficulty.EASY,
                    tableMin = 1,
                    tableMax = 10,
                    tableMultiplierMin = 1,
                    tableMultiplierMax = 10,
                    squareMin = 1,
                    squareMax = 10,
                    cubeMin = 1,
                    cubeMax = 5,
                    randomizeUnpredictable = false,
                    questionCount = 10
                )
                QuizDifficulty.MEDIUM -> QuizConfig(
                    category = currentCategory,
                    difficulty = QuizDifficulty.MEDIUM,
                    tableMin = 1,
                    tableMax = 20,
                    tableMultiplierMin = 1,
                    tableMultiplierMax = 10,
                    squareMin = 1,
                    squareMax = 20,
                    cubeMin = 1,
                    cubeMax = 15,
                    randomizeUnpredictable = true,
                    questionCount = 15
                )
                QuizDifficulty.HARD -> QuizConfig(
                    category = currentCategory,
                    difficulty = QuizDifficulty.HARD,
                    tableMin = 1,
                    tableMax = 30,
                    tableMultiplierMin = 1,
                    tableMultiplierMax = 20,
                    squareMin = 1,
                    squareMax = 35,
                    cubeMin = 1,
                    cubeMax = 25,
                    randomizeUnpredictable = true,
                    questionCount = 20
                )
                QuizDifficulty.CUSTOM -> QuizConfig(
                    category = currentCategory,
                    difficulty = QuizDifficulty.CUSTOM,
                    tableMin = 1,
                    tableMax = 20,
                    tableMultiplierMin = 1,
                    tableMultiplierMax = 10,
                    squareMin = 1,
                    squareMax = 20,
                    cubeMin = 1,
                    cubeMax = 15,
                    randomizeUnpredictable = true,
                    questionCount = 15
                )
            }
        }
    }
}
