package com.trans.integration.transacription

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
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
    val url = URL("https://api.assemblyai.com/v2/upload")
    val file = File(path)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Authorization", apiKey)
    connection.doOutput = true

    FileInputStream(file).use { input ->
        connection.outputStream.use { output ->
            input.copyTo(output)
        }
    }

//    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
//        throw Exception("Error: ${connection.responseCode} - ${connection.responseMessage}")
//    }

    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
    val uploadResponse = Json.decodeFromString<UploadResponse>(responseText)
    println(uploadResponse.upload_url)
    uploadResponse.upload_url
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
    println(responseText);
    val transcriptResponse = objectMapper.readValue(responseText, object : TypeReference<TranscriptResponse>(){})
    val pollingEndpoint = "https://api.assemblyai.com/v2/transcript/${transcriptResponse.id}"

    val pollingConnection = URL(pollingEndpoint).openConnection() as HttpURLConnection
    pollingConnection.requestMethod = "GET"
    pollingConnection.setRequestProperty("Authorization", apiKey)
//    delay(10000)
    val pollingResponseText = pollingConnection.inputStream.bufferedReader().use { it.readText() }
//    println(pollingResponseText)
    val pollingResponse = objectMapper.readValue(pollingResponseText, PollingResponse::class.java)
    println(pollingResponse.status)

}

fun main() {
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
