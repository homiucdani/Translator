package com.example.translator.feature_translator.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// post request
// those SerialName fields need to be exactly as there cuz the api says it
@Serializable
data class TranslateDto(
    @SerialName("q") val textToTranslate: String,
    @SerialName("source") val sourceLanguageCode: String,
    @SerialName("target") val targetLanguageCode: String
)
