package com.example.translator.feature_voice_to_text.domain

import com.example.translator.feature_voice_to_text.presentation.VoiceToTextParserState
import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextParser {

    val state: StateFlow<VoiceToTextParserState>
    fun startListening(languageCode: String)
    fun stopListening()
    fun cancelParser()
    fun resetParser()
}