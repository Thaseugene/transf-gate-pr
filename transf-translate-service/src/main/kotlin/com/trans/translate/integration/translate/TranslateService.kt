package com.trans.translate.integration.translate

import io.ktor.http.*
import com.trans.translate.model.request.TranslateRequest
import com.trans.translate.model.response.TranslateResponse
import com.trans.translate.integration.client.HttpClientService

public const val url = "https://api.lecto.ai/v1/translate/text"

interface TranslateService {

    suspend fun prepareTranslation(messageToTranslate: TranslateRequest): String

}

class TranslateServiceImpl(
    private val clientService: HttpClientService
) : TranslateService {

    private val apiKey: String = System.getenv("apiKey");

    private val requiredHeaders = mapOf(
        "X-API-Key" to apiKey,
        "Accept" to "application/json"
    )

    override suspend fun prepareTranslation(messageToTranslate: TranslateRequest): String {
        val response = clientService.callHttpService<TranslateResponse>(
            url,
            HttpMethod.Post,
            requiredHeaders,
            ContentType.Application.Json,
            messageToTranslate
        )
        response.translations?.let {
            return it[0].translated.first()
        }
        response.details?.let {
            return it.texts ?: throw RuntimeException()
        }
        throw RuntimeException()
    }

}
