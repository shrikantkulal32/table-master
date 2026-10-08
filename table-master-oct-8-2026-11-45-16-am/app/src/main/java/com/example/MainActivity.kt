package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.QuizViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MistakeBookScreen
import com.example.ui.screens.QuizResultScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ScoreboardScreen
import com.example.ui.screens.StudyChartsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            MyApplicationTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TableMasterApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.soundManager.stopMusic()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isMusicEnabled.value) {
            viewModel.soundManager.isMusicEnabled = true
        }
    }
}

@Composable
fun TableMasterApp(
    viewModel: QuizViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.HOME -> HomeScreen(viewModel = viewModel)
            AppScreen.QUIZ -> QuizScreen(viewModel = viewModel)
            AppScreen.RESULT -> QuizResultScreen(viewModel = viewModel)
            AppScreen.SCOREBOARD -> ScoreboardScreen(viewModel = viewModel)
            AppScreen.MISTAKES -> MistakeBookScreen(viewModel = viewModel)
            AppScreen.STUDY_CHARTS -> StudyChartsScreen(viewModel = viewModel)
        }
    }
}
