package translate.transf.com.service.mapping

import okhttp3.internal.immutableListOf
import translate.transf.com.dto.MessageStatus
import translate.transf.com.dto.ProcessingMessageRequest
import translate.transf.com.dto.ProcessingMessageResponse
import translate.transf.com.dto.TranslateRequest

fun ProcessingMessageRequest.toTranslateMessage() = TranslateRequest(
    immutableListOf(this.valueToTranslate),
    immutableListOf(this.lang)
)

fun String.toProcessingResponse(requestId: String) = ProcessingMessageResponse(
    requestId,
    this,
    MessageStatus.OK
)
