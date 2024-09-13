package com.trans.translate.service.mapping

import okhttp3.internal.immutableListOf
import com.trans.translate.model.MessageStatus
import com.trans.translate.model.request.TranslateMessageRequest
import com.trans.translate.model.response.TranslateMessageResponse
import com.trans.translate.model.request.TranslateRequest
import java.util.*

fun TranslateMessageRequest.toTranslateMessage() = TranslateRequest(
    immutableListOf(Base64.getDecoder().decode(this.valueToTranslate).decodeToString()),
    immutableListOf(this.lang)
)

fun String.toProcessingResponse(requestId: String, lang: String) = TranslateMessageResponse(
    requestId,
    Base64.getEncoder().encode(toByteArray()),
    lang,
    MessageStatus.OK
)
