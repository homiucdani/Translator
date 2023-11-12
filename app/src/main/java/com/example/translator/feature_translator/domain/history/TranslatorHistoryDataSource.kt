package com.example.translator.feature_translator.domain.history

import kotlinx.coroutines.flow.Flow

interface TranslatorHistoryDataSource {

    suspend fun upsertTranslateHistory(translatorHistory: TranslatorHistory)
    suspend fun deleteTranslateHistory(translatorHistory: TranslatorHistory)
    fun getHistory(): Flow<List<TranslatorHistory>>

}