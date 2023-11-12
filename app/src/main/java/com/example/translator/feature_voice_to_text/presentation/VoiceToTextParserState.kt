package com.example.translator.feature_voice_to_text.presentation


data class VoiceToTextParserState(
    val result: String = "",
    val error: String? = null,
    val powerRatio: Float = 0f, // max value 1f, used to draw onto canvas
    val isSpeaking: Boolean = false
)
