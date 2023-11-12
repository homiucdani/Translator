package com.example.translator.feature_voice_to_text.di

import android.app.Application
import com.example.translator.feature_voice_to_text.data.VoiceToTextParserImpl
import com.example.translator.feature_voice_to_text.domain.VoiceToTextParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VoiceToTextModule {

    @Provides
    @ViewModelScoped
    fun providesVoiceToTextParserImpl(app: Application): VoiceToTextParser {
        return VoiceToTextParserImpl(app)
    }
}