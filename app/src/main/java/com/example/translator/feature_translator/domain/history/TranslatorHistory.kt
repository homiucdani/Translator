package com.example.translator.feature_translator.domain.history

data class TranslatorHistory(
    val id: Int? = null,
    val fromLanguageCode: String,
    val fromText: String,
    val toLanguageCode: String,
    val toText: String,
    val timestamp: Long
)
