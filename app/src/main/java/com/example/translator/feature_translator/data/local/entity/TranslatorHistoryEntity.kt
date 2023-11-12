package com.example.translator.feature_translator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TranslatorHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val fromLanguageCode: String,
    val fromText: String,
    val toLanguageCode: String,
    val toText: String,
    val timestamp: Long
)
