package com.example.translator.feature_voice_to_text.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.feature_voice_to_text.domain.VoiceToTextParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceToTextViewModel @Inject constructor(
    private val voiceToTextParser: VoiceToTextParser
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceToTextState())

    // whenever our state changes or our voice to text triggers
    val state = _state.combine(voiceToTextParser.state) { state, voiceResult ->
        state.copy(
            spokenText = voiceResult.result,
            recordError = if (state.canRecord) {
                voiceResult.error // if true no error should be
            } else {
                "Can't record without permission"
            },
            displayState = when {
                !state.canRecord || voiceResult.error != null -> DisplayState.ERROR
                voiceResult.result.isNotBlank() && !voiceResult.isSpeaking -> DisplayState.DISPLAY_RESULTS
                voiceResult.isSpeaking -> DisplayState.SPEAKING
                else -> DisplayState.WAITING_TO_TALK
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VoiceToTextState())

    init {
        viewModelScope.launch {
            while (true) {
                if (state.value.displayState == DisplayState.SPEAKING) {
                    _state.update {
                        it.copy(
                            powerRatios = it.powerRatios + voiceToTextParser.state.value.powerRatio
                        )
                    }
                }
                delay(50L)// update ui 20 times in 1 sec
            }
        }
    }

    fun onEvent(event: VoiceToTextEvent) {
        when (event) {
            is VoiceToTextEvent.PermissionResult -> {
                _state.update {
                    it.copy(
                        canRecord = event.isGranted
                    )
                }
            }

            is VoiceToTextEvent.Reset -> {
                voiceToTextParser.resetParser()
                _state.value = VoiceToTextState()
            }

            is VoiceToTextEvent.ToggleRecording -> {
                toggleRecording(event.languageCode)
            }

            else -> Unit
        }
    }

    private fun toggleRecording(languageCode: String) {
        // we wanna start with a new list when is toggle on off, on
        _state.update {
            it.copy(powerRatios = emptyList())
        }
        voiceToTextParser.cancelParser()
        // of we are speaking and press the button then we wanna stop speaking
        if (state.value.displayState == DisplayState.SPEAKING) {
            voiceToTextParser.stopListening()
        } else {
            voiceToTextParser.startListening(languageCode)
        }
    }
}