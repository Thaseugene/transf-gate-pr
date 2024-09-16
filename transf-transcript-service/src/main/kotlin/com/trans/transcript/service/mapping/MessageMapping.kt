package com.trans.transcript.service.mapping

import com.trans.transcript.model.MessageStatus
import com.trans.transcript.model.request.TranscriptionMessageRequest

fun prepareProcessingResponse(
    requestId: String,
    status: MessageStatus,
    result: String
) = TranscriptionMessageRequest(
    requestId,
    result,
    status
)
