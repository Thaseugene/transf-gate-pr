package com.trans.translate.service.mapping

import okhttp3.internal.immutableListOf
import com.trans.translate.model.MessageStatus
import com.trans.translate.model.request.ProcessingMessageRequest
import com.trans.translate.model.response.ProcessingMessageResponse
import com.trans.translate.model.request.TranslateRequest
import java.util.*

fun ProcessingMessageRequest.toTranslateMessage() = TranslateRequest(
    immutableListOf(Base64.getDecoder().decode(this.valueToTranslate).decodeToString()),
    immutableListOf(this.lang)
)

fun String.toProcessingResponse(requestId: String, lang: String) = ProcessingMessageResponse(
    requestId,
    Base64.getEncoder().encode(this.toByteArray()),
    lang,
    MessageStatus.OK
)
