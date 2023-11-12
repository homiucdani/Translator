package com.example.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.translator.core.domain.presentation.Routes
import com.example.translator.feature_translator.presentation.TranslateEvent
import com.example.translator.feature_translator.presentation.TranslateScreen
import com.example.translator.feature_translator.presentation.TranslateViewModel
import com.example.translator.feature_voice_to_text.presentation.VoiceToTextEvent
import com.example.translator.feature_voice_to_text.presentation.VoiceToTextScreen
import com.example.translator.feature_voice_to_text.presentation.VoiceToTextViewModel
import com.example.translator.ui.theme.TranslatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslatorTheme {
                TranslateRoot()
            }
        }
    }
}

@Composable
fun TranslateRoot() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.TRANSLATE) {

        composable(Routes.TRANSLATE) {
            val translateViewModel: TranslateViewModel = hiltViewModel()
            val state = translateViewModel.state.collectAsState()

            val voiceResult = it.savedStateHandle.getStateFlow<String?>(
                "voiceResult", null
            ).collectAsState()

            // this event needs to be executed automatically, we cannot put it inside translateScreen, cuz events from there are triggered by the user
            LaunchedEffect(key1 = voiceResult) {
                translateViewModel.onEvent(TranslateEvent.SubmitVoiceResult(voiceResult.value))
                it.savedStateHandle["voiceResult"] = null // clear
            }

            TranslateScreen(
                state = state.value,
                onEvent = { event ->
                    when (event) {
                        is TranslateEvent.RecordAudio -> {
                            navController.navigate(
                                Routes.VOICE_TO_TEXT + "/${state.value.fromLanguage.language.langCode}"
                            )
                        }

                        else -> {
                            translateViewModel.onEvent(event)
                        }
                    }
                }
            )
        }

        // which language is the current user speaking, voice to text needs to know
        composable(
            Routes.VOICE_TO_TEXT + "/{languageCode}",
            arguments = listOf(
                navArgument(
                    name = "languageCode",
                ) {
                    type = NavType.StringType
                    defaultValue = "en"
                }
            )
        ) { args ->
            val viewModel: VoiceToTextViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            val langCode = args.arguments?.getString("languageCode") ?: "en"

            VoiceToTextScreen(
                state = state.value,
                languageCode = langCode,
                onResult = { spokenText ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "voiceResult", spokenText
                    )
                    navController.popBackStack()
                },
                onEvent = { event ->
                    when (event) {
                        is VoiceToTextEvent.Close -> {
                            navController.popBackStack()
                        }

                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
    }
}