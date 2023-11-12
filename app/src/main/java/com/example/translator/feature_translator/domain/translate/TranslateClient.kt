package com.example.translator.feature_translator.domain.translate

import com.example.translator.core.domain.language.Language

interface TranslateClient {

    suspend fun translate(
        fromLanguage: Language,
        fromText: String,  // which text we wanna translate
        toLanguage: Language
    ): String

    companion object {
        const val BASE_URL = "https://translate.pl-coding.com"
    }
}