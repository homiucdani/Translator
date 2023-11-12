package com.example.translator.feature_voice_to_text.data

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.translator.R
import com.example.translator.feature_voice_to_text.domain.VoiceToTextParser
import com.example.translator.feature_voice_to_text.presentation.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VoiceToTextParserImpl(private val app: Application) : VoiceToTextParser, RecognitionListener {

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(app)

    // write
    private val _state = MutableStateFlow(VoiceToTextParserState())

    // read only
    override val state: StateFlow<VoiceToTextParserState>
        get() = _state.asStateFlow()

    override fun startListening(languageCode: String) {
        // start with a new state, cuz can be leaks from the other state
        _state.value = VoiceToTextParserState()

        if (!SpeechRecognizer.isRecognitionAvailable(app)) {
            _state.update {
                it.copy(
                    error = app.getString(R.string.recognizer_not_available)
                )
            }
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }

        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _state.update {
            it.copy(
                isSpeaking = true
            )
        }
    }

    override fun stopListening() {
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
        recognizer.stopListening()
    }

    override fun cancelParser() {
        recognizer.cancel()
    }

    override fun resetParser() {
        _state.value = VoiceToTextParserState()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        // if the api is ready then we got no error
        _state.update {
            it.copy(
                error = null
            )
        }
    }

    override fun onBeginningOfSpeech() = Unit

    // how loud the user is speaking
    override fun onRmsChanged(rmsdB: Float) {
        _state.update {
            it.copy(
                // here we calculate the interval to get a better value between 0 and 1
                powerRatio = rmsdB * (1f / (12f - (-2f))) // max value 12f, min val -2
            )
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(error: Int) {
        if (error == SpeechRecognizer.ERROR_CLIENT ) {
            return
        }else if (error == 7){
            _state.update {
                it.copy(
                    error = "Speak loudly"
                )
            }
            return
        }
        _state.update {
            it.copy(
                error = "Error: $error"
            )
        }
    }

    override fun onResults(results: Bundle?) {
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0) // our result is at the index 0
            ?.let { text ->
                _state.update {
                    it.copy(
                        result = text
                    )
                }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    override fun onEvent(eventType: Int, params: Bundle?) = Unit
}