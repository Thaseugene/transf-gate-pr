package translate.transf.com.integration.translate

import io.ktor.http.*
import okhttp3.internal.immutableListOf
import translate.transf.com.dto.TranslateRequest
import translate.transf.com.dto.TranslateResponse
import translate.transf.com.integration.client.HttpClientService

public const val url = "https://api.lecto.ai/v1/translate/text"

suspend fun main() {
    val service = HttpClientService()
    val headers = mutableMapOf(
        "X-API-Key" to "TDDV0VS-V7DMYXH-HXQYE3N-N2JWQS6",
        "Content-Type" to "application/json",
        "Accept"  to "application/json"
    )
    val body = TranslateRequest(
        immutableListOf("What are you waiting for?"),
        immutableListOf("ru")
    )
    val response = service.callHttpService<TranslateResponse>(
        url,
        HttpMethod.Post,
        headers,
        body = body
    )
    println(response.translations.first().translated.first())
}
