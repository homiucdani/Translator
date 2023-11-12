package com.example.translator.feature_translator.domain.use_case

import com.example.translator.core.domain.language.Language
import com.example.translator.feature_translator.domain.history.TranslatorHistory
import com.example.translator.feature_translator.domain.history.TranslatorHistoryDataSource
import com.example.translator.feature_translator.domain.translate.TranslateClient
import com.example.translator.feature_translator.domain.translate.TranslateException

class Translate(
    private val translateClient: TranslateClient,
    private val translatordataSource: TranslatorHistoryDataSource
) {

    suspend operator fun invoke(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): Result<String> {

        return try {
            //if this have an error the code blow is skipped and goes straight to catch
            val translatedText = translateClient.translate(fromLanguage, fromText, toLanguage)

            // insert into db when we press search
            translatordataSource.upsertTranslateHistory(
                TranslatorHistory(
                    fromLanguageCode = fromLanguage.langCode,
                    fromText = fromText,
                    toLanguageCode = toLanguage.langCode,
                    toText = translatedText,
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(translatedText)
        } catch (e: TranslateException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}