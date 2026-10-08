package com.example.quiz

import com.example.data.model.Question
import com.example.data.model.QuizCategory
import com.example.data.model.QuizConfig
import kotlin.random.Random

object QuestionGenerator {

    fun generateQuestions(config: QuizConfig, count: Int): List<Question> {
        val targetCount = if (count <= 0) 15 else count
        val questions = mutableListOf<Question>()
        var attempts = 0
        val usedPrompts = mutableSetOf<String>()

        while (questions.size < targetCount && attempts < targetCount * 5) {
            attempts++
            val q = generateSingleQuestion(config)
            if (usedPrompts.add(q.prompt)) {
                questions.add(q)
            }
        }

        // If not enough unique, fill with variations
        while (questions.size < targetCount) {
            questions.add(generateSingleQuestion(config))
        }

        return questions
    }

    fun generateSingleQuestion(config: QuizConfig): Question {
        return when (config.category) {
            QuizCategory.TABLES -> generateTableQuestion(config)
            QuizCategory.SQUARES -> generateSquareQuestion(config)
            QuizCategory.CUBES -> generateCubeQuestion(config)
            QuizCategory.RANDOM_CHALLENGE -> generateMixedQuestion(config)
        }
    }

    private fun generateTableQuestion(config: QuizConfig): Question {
        val minT = config.tableMin.coerceAtLeast(1)
        val maxT = config.tableMax.coerceAtLeast(minT)
        val minM = config.tableMultiplierMin.coerceAtLeast(1)
        val maxM = config.tableMultiplierMax.coerceAtLeast(minM)

        if (config.randomizeUnpredictable && Random.nextFloat() < 0.35f) {
            // Unpredictable variations
            val variantType = Random.nextInt(2)
            if (variantType == 0) {
                // Arbitrary 2-number multiplication within the max table range (e.g. 14 × 17)
                val low = minT.coerceAtLeast(2)
                val high = maxT.coerceAtLeast(low)
                val a = Random.nextInt(low, high + 1)
                val b = Random.nextInt(low, high + 1)
                val ans = a * b
                val prompt = "$a × $b = ?"
                val explanation = "$a × $b = $ans"
                val options = generateDistractors(ans, baseA = a, baseB = b, type = DistractorType.MULTIPLICATION)
                return Question(
                    prompt = prompt,
                    categoryTag = "Arbitrary Multiply ($a × $b)",
                    correctAnswer = ans,
                    options = options,
                    explanation = explanation,
                    category = QuizCategory.TABLES,
                    isUnpredictable = true
                )
            } else {
                // Missing factor question: e.g. 16 × ? = 112
                val a = Random.nextInt(minT, maxT + 1)
                val m = Random.nextInt(minM, maxM + 1)
                val product = a * m
                val prompt = "$a × ? = $product"
                val explanation = "$a × $m = $product (Missing factor is $m)"
                val options = generateDistractorsForFactor(m)
                return Question(
                    prompt = prompt,
                    categoryTag = "Missing Factor",
                    correctAnswer = m,
                    options = options,
                    explanation = explanation,
                    category = QuizCategory.TABLES,
                    isUnpredictable = true
                )
            }
        }

        // Standard Table question
        val tableNum = Random.nextInt(minT, maxT + 1)
        val mult = Random.nextInt(minM, maxM + 1)
        val answer = tableNum * mult
        val prompt = "$tableNum × $mult = ?"
        val explanation = "$tableNum × $mult = $answer"
        val options = generateDistractors(answer, baseA = tableNum, baseB = mult, type = DistractorType.MULTIPLICATION)

        return Question(
            prompt = prompt,
            categoryTag = "Table of $tableNum",
            correctAnswer = answer,
            options = options,
            explanation = explanation,
            category = QuizCategory.TABLES
        )
    }

    private fun generateSquareQuestion(config: QuizConfig): Question {
        val minS = config.squareMin.coerceAtLeast(1)
        val maxS = config.squareMax.coerceAtLeast(minS)
        val n = Random.nextInt(minS, maxS + 1)

        if (config.randomizeUnpredictable && Random.nextFloat() < 0.30f) {
            // Reverse square root: √324 = ?
            val sq = n * n
            val prompt = "√$sq = ?"
            val explanation = "√$sq = $n (since $n² = $sq)"
            val options = generateDistractorsForFactor(n)
            return Question(
                prompt = prompt,
                categoryTag = "Square Root (√)",
                correctAnswer = n,
                options = options,
                explanation = explanation,
                category = QuizCategory.SQUARES,
                isUnpredictable = true
            )
        }

        val ans = n * n
        val prompt = "$n² = ?"
        val explanation = "$n² = $n × $n = $ans"
        val options = generateDistractors(ans, baseA = n, baseB = n, type = DistractorType.SQUARE)

        return Question(
            prompt = prompt,
            categoryTag = "Square ($n²)",
            correctAnswer = ans,
            options = options,
            explanation = explanation,
            category = QuizCategory.SQUARES
        )
    }

    private fun generateCubeQuestion(config: QuizConfig): Question {
        val minC = config.cubeMin.coerceAtLeast(1)
        val maxC = config.cubeMax.coerceAtLeast(minC)
        val n = Random.nextInt(minC, maxC + 1)

        if (config.randomizeUnpredictable && Random.nextFloat() < 0.30f) {
            // Reverse cube root: ³√1728 = ?
            val cube = n * n * n
            val prompt = "³√$cube = ?"
            val explanation = "³√$cube = $n (since $n³ = $cube)"
            val options = generateDistractorsForFactor(n)
            return Question(
                prompt = prompt,
                categoryTag = "Cube Root (³√)",
                correctAnswer = n,
                options = options,
                explanation = explanation,
                category = QuizCategory.CUBES,
                isUnpredictable = true
            )
        }

        val ans = n * n * n
        val prompt = "$n³ = ?"
        val explanation = "$n³ = $n × $n × $n = $ans"
        val options = generateDistractors(ans, baseA = n, baseB = n, type = DistractorType.CUBE)

        return Question(
            prompt = prompt,
            categoryTag = "Cube ($n³)",
            correctAnswer = ans,
            options = options,
            explanation = explanation,
            category = QuizCategory.CUBES
        )
    }

    private fun generateMixedQuestion(config: QuizConfig): Question {
        val roll = Random.nextInt(3)
        return when (roll) {
            0 -> generateTableQuestion(config)
            1 -> generateSquareQuestion(config)
            else -> generateCubeQuestion(config)
        }
    }

    private enum class DistractorType {
        MULTIPLICATION, SQUARE, CUBE
    }

    private fun generateDistractors(correct: Int, baseA: Int, baseB: Int, type: DistractorType): List<Int> {
        val distractors = mutableSetOf<Int>()

        when (type) {
            DistractorType.MULTIPLICATION -> {
                // Plausible calculation errors
                if (baseA > 1) distractors.add((baseA - 1) * baseB)
                distractors.add((baseA + 1) * baseB)
                if (baseB > 1) distractors.add(baseA * (baseB - 1))
                distractors.add(baseA * (baseB + 1))
                distractors.add(correct + 10)
                if (correct > 10) distractors.add(correct - 10)
                distractors.add(correct + baseA)
                if (correct > baseA) distractors.add(correct - baseA)
            }
            DistractorType.SQUARE -> {
                if (baseA > 1) distractors.add((baseA - 1) * (baseA - 1))
                distractors.add((baseA + 1) * (baseA + 1))
                if (baseA > 2) distractors.add((baseA - 2) * (baseA - 2))
                distractors.add((baseA + 2) * (baseA + 2))
                distractors.add(correct + 10)
                if (correct > 10) distractors.add(correct - 10)
                distractors.add(correct + 20)
            }
            DistractorType.CUBE -> {
                if (baseA > 1) distractors.add((baseA - 1) * (baseA - 1) * (baseA - 1))
                distractors.add((baseA + 1) * (baseA + 1) * (baseA + 1))
                distractors.add(correct + 10)
                if (correct > 10) distractors.add(correct - 10)
                distractors.add(correct + 100)
                if (correct > 100) distractors.add(correct - 100)
            }
        }

        distractors.remove(correct)
        // Clean out zero or negatives
        val validCandidates = distractors.filter { it > 0 }.shuffled().toMutableList()

        val selected = mutableSetOf<Int>()
        selected.add(correct)

        for (cand in validCandidates) {
            if (cand != correct) {
                selected.add(cand)
                if (selected.size == 4) break
            }
        }

        // If we still need more options, generate nearby offsets
        var offset = 1
        while (selected.size < 4) {
            val high = correct + offset
            val low = (correct - offset).coerceAtLeast(1)
            if (high != correct) selected.add(high)
            if (selected.size < 4 && low != correct) selected.add(low)
            offset += Random.nextInt(2, 6)
        }

        return selected.shuffled()
    }

    private fun generateDistractorsForFactor(factor: Int): List<Int> {
        val selected = mutableSetOf(factor)
        val pool = listOf(
            factor - 1, factor + 1,
            factor - 2, factor + 2,
            factor - 3, factor + 3,
            factor + 4, factor - 4
        ).filter { it > 0 }.shuffled()

        for (cand in pool) {
            selected.add(cand)
            if (selected.size == 4) break
        }

        var delta = 1
        while (selected.size < 4) {
            val h = factor + delta
            val l = (factor - delta).coerceAtLeast(1)
            if (h != factor) selected.add(h)
            if (selected.size < 4 && l != factor) selected.add(l)
            delta += 2
        }

        return selected.shuffled()
    }
}
