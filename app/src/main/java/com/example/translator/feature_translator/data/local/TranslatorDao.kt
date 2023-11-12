package com.example.translator.feature_translator.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.translator.feature_translator.data.local.entity.TranslatorHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslatorDao {

    @Upsert
    suspend fun upsertTranslateHistory(translatorHistoryEntity: TranslatorHistoryEntity)

    @Delete
    suspend fun deleteTranslateHistory(translatorHistoryEntity: TranslatorHistoryEntity)

    @Query("SELECT * FROM translatorhistoryentity ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<TranslatorHistoryEntity>>
}