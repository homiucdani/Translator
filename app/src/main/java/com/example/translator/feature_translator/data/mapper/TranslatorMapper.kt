package com.example.translator.feature_translator.data.mapper

import com.example.translator.feature_translator.data.local.entity.TranslatorHistoryEntity
import com.example.translator.feature_translator.domain.history.TranslatorHistory

fun TranslatorHistoryEntity.toTranslatorHistory(): TranslatorHistory {
    return TranslatorHistory(
        id = id,
        fromLanguageCode = fromLanguageCode,
        fromText = fromText,
        toLanguageCode = toLanguageCode,
        toText = toText,
        timestamp = timestamp
    )
}

fun TranslatorHistory.toTranslatorHistoryEntity(): TranslatorHistoryEntity {
    return TranslatorHistoryEntity(
        id = id,
        fromLanguageCode = fromLanguageCode,
        fromText = fromText,
        toLanguageCode = toLanguageCode,
        toText = toText,
        timestamp = timestamp
    )
}