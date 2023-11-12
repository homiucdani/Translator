package com.example.translator.feature_translator.presentation

import com.example.translator.core.domain.presentation.UiLanguage
import com.example.translator.feature_translator.domain.translate.TranslateException

data class TranslateState(
    val fromText: String = "",
    val toText: String? = null,
    val isTranslating: Boolean = false,
    val fromLanguage: UiLanguage = UiLanguage.byCode("en"),
    val toLanguage: UiLanguage = UiLanguage.byCode("de"),
    val isChoosingFromLanguage: Boolean = false,
    val isChoosingToLanguage: Boolean = false,
    val historyList: List<UiHistoryItem> = emptyList(),
    val error: TranslateException? = null
)
