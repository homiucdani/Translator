package com.example.translator.feature_translator.data.history

import com.example.translator.feature_translator.data.local.TranslatorDao
import com.example.translator.feature_translator.data.mapper.toTranslatorHistory
import com.example.translator.feature_translator.data.mapper.toTranslatorHistoryEntity
import com.example.translator.feature_translator.domain.history.TranslatorHistory
import com.example.translator.feature_translator.domain.history.TranslatorHistoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TranslatorHistoryDataSourceImpl(
    private val dao: TranslatorDao
) : TranslatorHistoryDataSource {

    override suspend fun upsertTranslateHistory(translatorHistory: TranslatorHistory) {
        dao.upsertTranslateHistory(translatorHistory.toTranslatorHistoryEntity())
    }

    override suspend fun deleteTranslateHistory(translatorHistory: TranslatorHistory) {
        dao.deleteTranslateHistory(translatorHistory.toTranslatorHistoryEntity())
    }

    override fun getHistory(): Flow<List<TranslatorHistory>> {
        return dao.getHistory().map { history -> history.map { it.toTranslatorHistory() } }
    }
}