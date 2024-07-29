package com.trans.integration.transacription

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class UploadResponse(val upload_url: String)

@Serializable
data class TranscriptRequest(val audio_url: String)

@Serializable
data class TranscriptResponse(val id: String)

@Serializable
data class PollingResponse(val status: String, val text: String? = null, val error: String? = null)

suspend fun uploadFile(path: String, apiKey: String): String = withContext(IO) {
    val client = HttpClient(CIO) {

        install(Logging) {
            level = LogLevel.INFO
        }

    }
    val url = URL("https://api.assemblyai.com/v2/upload")
    val file = File(path)
    val response: HttpResponse = client.post("https://api.assemblyai.com/v2/upload") {
        contentType(ContentType.Application.OctetStream)
        bearerAuth(apiKey)
        setBody(file.readChannel())
    }

    val objectMapper = jacksonObjectMapper()
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    val transcriptResponse = objectMapper.readValue(response.bodyAsText(), object : TypeReference<UploadResponse>() {})
    println(transcriptResponse.upload_url)
    transcriptResponse.upload_url
}

suspend fun getTranscript(audioUrl: String, apiKey: String) = withContext(IO) {
    val url = URL("https://api.assemblyai.com/v2/transcript")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Authorization", apiKey)
    connection.setRequestProperty("Content-Type", "application/json")
    connection.doOutput = true

    val objectMapper = jacksonObjectMapper()

    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    val requestData = objectMapper.writeValueAsString(TranscriptRequest(audioUrl))
    connection.outputStream.use { output ->
        output.write(requestData.toByteArray(Charsets.UTF_8))
    }

    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
    val transcriptResponse = objectMapper.readValue(responseText, object : TypeReference<TranscriptResponse>() {})
    val pollingEndpoint = "https://api.assemblyai.com/v2/transcript/${transcriptResponse.id}"

    val pollingConnection = URL(pollingEndpoint).openConnection() as HttpURLConnection
//    pollingConnection.requestMethod = "GET"
//    pollingConnection.setRequestProperty("Authorization", apiKey)
//    delay(10000)
//    val pollingResponseText = pollingConnection.inputStream.bufferedReader().use { it.readText() }
//    println(pollingResponseText)
//    val pollingResponse = objectMapper.readValue(pollingResponseText, PollingResponse::class.java)

    println(checkResult(pollingEndpoint, apiKey).text)

}

suspend fun checkResult(pollingEndpoint: String, apiKey: String): PollingResponse {
    val objectMapper = jacksonObjectMapper()
    val pollingConnection = withContext(IO) {
        URL(pollingEndpoint).openConnection()
    } as HttpURLConnection
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    pollingConnection.requestMethod = "GET"
    pollingConnection.setRequestProperty("Authorization", apiKey)
    val pollingResponseText = pollingConnection.inputStream.bufferedReader().use { it.readText() }
    val pollingResponse = objectMapper.readValue(pollingResponseText, PollingResponse::class.java)
    when (getStatus(pollingResponse)) {
        TranscriptionTaskStatus.ERROR -> throw RuntimeException("Task was ended with error")
        TranscriptionTaskStatus.PROCESSING -> {
            delay(3000L)
            return checkResult(pollingEndpoint, apiKey)
        }

        TranscriptionTaskStatus.QUEUED -> {
            delay(3000L)
            return checkResult(pollingEndpoint, apiKey)
        }

        TranscriptionTaskStatus.COMPLETED -> return pollingResponse
    }
    throw RuntimeException("Task ended without status")
}

suspend fun getStatus(pollingResponse: PollingResponse): TranscriptionTaskStatus {
    println(pollingResponse.status)
    return TranscriptionTaskStatus.valueOf(pollingResponse.status.uppercase())
}

fun main() {
//    println(TranscriptionTaskStatus.valueOf("processing".toUpperCasePreservingASCIIRules()))
    val apiKey = "c4dcb2208a1c4c8088b4b7787f4f7cfe"
    val path = "Nice Talking with You.mp3"

    GlobalScope.launch {
        try {
            val uploadedFileUrl = uploadFile(path, apiKey)
            getTranscript(uploadedFileUrl, apiKey)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }

    // Keep the main thread alive to allow the coroutine to complete
    Thread.sleep(30000)
}
