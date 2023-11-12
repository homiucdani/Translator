package com.example.translator.feature_translator.presentation.components

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


// its a composable cuz when this composable leaves to composition(user exit the app or enter another screen)
// then we dont have any leaks and everything is disposed
@Composable
fun rememberTextToSpeech(): TextToSpeech {
    val context = LocalContext.current
    val tts = remember {
        TextToSpeech(context, null)
    }

    // when this composable leaves the composition then we wanna stop, shutdown our tts
    DisposableEffect(key1 = tts){
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
    return tts
}