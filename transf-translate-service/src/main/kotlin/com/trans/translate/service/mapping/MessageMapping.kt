package com.trans.translate.service.mapping

import okhttp3.internal.immutableListOf
import com.trans.translate.model.MessageStatus
import com.trans.translate.model.request.TranslateMessageRequest
import com.trans.translate.model.response.TranslateMessageResponse
import com.trans.translate.model.request.TranslateRequest
import java.util.*

fun TranslateMessageRequest.toTranslateMessage(text: String) = TranslateRequest(
    immutableListOf(text),
    immutableListOf(this.lang)
)

fun String.toProcessingResponse(requestId: String, lang: String) = TranslateMessageResponse(
    requestId,
    Base64.getEncoder().encode(toByteArray()),
    lang,
    MessageStatus.OK
)

fun prepareTextInput(input: ByteArray): List<String> {
    val valueToTranslate = Base64.getDecoder().decode(input).decodeToString()
    val maxLength = 1000
    val sentences = valueToTranslate.split(".")
    val result = mutableListOf<String>()
    var currentPart = StringBuilder()

    for (sentence in sentences) {
        val trimmedSentence = sentence.trim()
        if (currentPart.length + trimmedSentence.length + 1 > maxLength) {
            result.add(currentPart.toString().trim())
            currentPart = StringBuilder()
        }
        if (trimmedSentence.isNotEmpty()) {
            currentPart.append(trimmedSentence).append(". ")
        }
    }
    if (currentPart.isNotEmpty()) {
        result.add(currentPart.toString().trim())
    }
    return result
}
