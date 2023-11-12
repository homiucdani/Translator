package com.example.translator.di

import android.app.Application
import androidx.room.Room
import com.example.translator.feature_translator.data.history.TranslatorHistoryDataSourceImpl
import com.example.translator.feature_translator.data.local.TranslatorDao
import com.example.translator.feature_translator.data.local.TranslatorDatabase
import com.example.translator.feature_translator.data.remote.KtorTranslateClient
import com.example.translator.feature_translator.domain.history.TranslatorHistoryDataSource
import com.example.translator.feature_translator.domain.translate.TranslateClient
import com.example.translator.feature_translator.domain.use_case.Translate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun providesTranslatorDatabase(app: Application): TranslatorDatabase {
        return Room.databaseBuilder(
            app,
            TranslatorDatabase::class.java,
            "translator_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesKtorHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    @Provides
    @Singleton
    fun providesTranslateClient(httpClient: HttpClient): TranslateClient {
        return KtorTranslateClient(httpClient)
    }

    @Provides
    @Singleton
    fun providesTranslatorDao(db: TranslatorDatabase): TranslatorDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun providesTranslatorHistoryDataSource(translatorDao: TranslatorDao): TranslatorHistoryDataSource {
        return TranslatorHistoryDataSourceImpl(translatorDao)
    }

    @Provides
    @Singleton
    fun providesTranslateUseCase(
        translateClient: TranslateClient,
        translatorHistoryDataSource: TranslatorHistoryDataSource
    ): Translate {
        return Translate(translateClient, translatorHistoryDataSource)
    }

}