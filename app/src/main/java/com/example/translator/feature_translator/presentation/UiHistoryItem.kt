package com.example.translator.feature_translator.presentation

import com.example.translator.core.domain.presentation.UiLanguage

data class UiHistoryItem(
    val id: Int,
    val fromText: String,
    val toText: String,
    val fromLanguage: UiLanguage,
    val toLanguage: UiLanguage
)