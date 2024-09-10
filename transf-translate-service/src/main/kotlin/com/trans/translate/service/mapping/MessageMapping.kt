package com.trans.translate.service.mapping

import okhttp3.internal.immutableListOf
import com.trans.translate.dto.MessageStatus
import com.trans.translate.dto.ProcessingMessageRequest
import com.trans.translate.dto.ProcessingMessageResponse
import com.trans.translate.dto.TranslateRequest
import java.util.*

fun ProcessingMessageRequest.toTranslateMessage() = TranslateRequest(
    immutableListOf(this.valueToTranslate),
    immutableListOf(this.lang)
)

fun String.toProcessingResponse(requestId: String, lang: String) = ProcessingMessageResponse(
    requestId,
    Base64.getEncoder().encode(this.toByteArray()),
    lang,
    MessageStatus.OK
)
