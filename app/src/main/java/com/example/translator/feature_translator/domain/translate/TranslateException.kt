package com.example.translator.feature_translator.domain.translate

sealed class TranslateException(private val errorMessage: String) : Exception(errorMessage) {
    data class ServiceUnavailable(val error:String) : TranslateException("An error occurred when translating: $error")
    data class ClientError(val error: String) : TranslateException("An error occurred when translating: $error")
    data class ServerError(val error:String) : TranslateException("An error occurred when translating: $error")
    data class UnknownError(val error:String) : TranslateException("An error occurred when translating: $error")
}
