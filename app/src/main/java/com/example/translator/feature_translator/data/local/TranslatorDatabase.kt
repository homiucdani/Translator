package com.example.translator.feature_translator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.translator.feature_translator.data.local.entity.TranslatorHistoryEntity

@Database(
    entities = [
        TranslatorHistoryEntity::class
    ],
    version = 1
)
abstract class TranslatorDatabase : RoomDatabase() {
    abstract val dao:TranslatorDao
}