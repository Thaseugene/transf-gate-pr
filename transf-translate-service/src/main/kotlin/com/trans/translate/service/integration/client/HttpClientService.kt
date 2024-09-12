package com.trans.translate.service.integration.client

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

class HttpClientService {

    val client: HttpClient = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            jackson {
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            socketTimeoutMillis = 60000
            connectTimeoutMillis = 60000
        }
    }

    suspend inline fun <reified T> callHttpService(
        url: String,
        method: HttpMethod,
        headers: Map<String, String> = emptyMap(),
        contentType: ContentType? = null,
        body: Any? = null
    ): T {
        return client.request(url) {
            this.method = method
            contentType?. let { this.contentType(it) }
            headers.forEach { entry -> header(entry.key, entry.value) }
            body?.let { setBody(it) }
        }.body()
    }

}
