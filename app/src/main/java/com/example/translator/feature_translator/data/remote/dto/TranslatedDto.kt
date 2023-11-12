package com.example.translator.feature_translator.data.remote.dto

import kotlinx.serialization.Serializable

// get request response
// this is the object that we get from the response, cuz we make a post request to the server with "TranslateDto" class
@Serializable
data class TranslatedDto(
    val translatedText: String  // this name is used by the api, if changed than good luck on debug
)
