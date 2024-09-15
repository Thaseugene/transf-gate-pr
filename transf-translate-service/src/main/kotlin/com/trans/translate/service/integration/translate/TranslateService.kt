package com.trans.translate.service.integration.translate

import com.trans.translate.configuration.TranslateConfiguration
import io.ktor.http.*
import com.trans.translate.model.request.TranslateRequest
import com.trans.translate.model.response.TranslateResponse
import com.trans.translate.service.integration.client.HttpClientService

interface TranslateService {

    suspend fun prepareTranslation(messageToTranslate: TranslateRequest): String

}

class TranslateServiceImpl(
    private val clientService: HttpClientService
) : TranslateService {

    private val requiredHeaders = mapOf(
        "X-API-Key" to TranslateConfiguration.TRANSLATION_API_TOKEN,
        HttpHeaders.Accept to "application/json"
    )

    override suspend fun prepareTranslation(messageToTranslate: TranslateRequest): String {
        val response = clientService.callHttpService<TranslateResponse>(
            TranslateConfiguration.API_URL,
            HttpMethod.Post,
            requiredHeaders,
            ContentType.Application.Json,
            messageToTranslate
        )
        response.translations?.let {
            return it[0].translated.reduce { acc, s -> acc.plus(s) }
        }
        response.details?.let {
            return it.texts ?: throw RuntimeException()
        }
        throw RuntimeException()
    }

}
