package com.example.translator.feature_translator.data.remote

import com.example.translator.core.domain.language.Language
import com.example.translator.feature_translator.data.remote.dto.TranslateDto
import com.example.translator.feature_translator.data.remote.dto.TranslatedDto
import com.example.translator.feature_translator.domain.translate.TranslateClient
import com.example.translator.feature_translator.domain.translate.TranslateException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import okio.IOException

class KtorTranslateClient(
    private val httpClient: HttpClient // this is the ktor http client
) : TranslateClient {

    override suspend fun translate(
        fromLanguage: Language,
        fromText: String,
        toLanguage: Language
    ): String {
        // is a network call so use try catch
        val result = try {
            httpClient.post {
                url(TranslateClient.BASE_URL + "/translate")
                contentType(ContentType.Application.Json) // what we get from the post request
                // this is the request to the server, we make it with our class, than we get the result
                setBody(
                    TranslateDto(
                        textToTranslate = fromText,
                        sourceLanguageCode = fromLanguage.langCode,
                        targetLanguageCode = toLanguage.langCode
                    )
                )
            }
        } catch (e: IOException) {
            throw TranslateException.ServiceUnavailable("Service Unavailable")
        }

        // check the result for errors
        when (result.status.value) {
            in 200..299 -> Unit // the answer is good
            500 -> throw TranslateException.ServerError("Server Error")
            in 400..499 -> throw TranslateException.ClientError("Client Error")
            else -> throw TranslateException.UnknownError("Unknown Error")
        }

        // what we want to get from the "result", make another check if the result cannot be parsed to the "TranslatedDto"
        return try {
            result.body<TranslatedDto>().translatedText
        } catch (e: Exception) {
            throw TranslateException.ServerError("Server Error")
        }
    }
}