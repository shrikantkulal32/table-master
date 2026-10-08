package com.example.data.model

enum class QuizCategory(val displayName: String, val description: String) {
    TABLES("Multiplication Tables", "Practice tables like 1 to 20"),
    SQUARES("Squares (x²)", "Master powers of 2 up to 50²"),
    CUBES("Cubes (x³)", "Calculate cubes up to 30³"),
    RANDOM_CHALLENGE("Random Challenge", "Tables, squares, cubes & unexpected arithmetic")
}

enum class QuizDifficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    CUSTOM("Custom")
}
