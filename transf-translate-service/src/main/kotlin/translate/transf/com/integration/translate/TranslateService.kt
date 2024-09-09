package translate.transf.com.integration.translate

import io.ktor.http.*
import translate.transf.com.dto.TranslateRequest
import translate.transf.com.dto.TranslateResponse
import translate.transf.com.integration.client.HttpClientService

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
        return response.translations.first().translated.first()
    }

}
