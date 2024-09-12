package com.trans.transcript.service.mapping

import com.transf.kafka.messaging.common.model.MessageStatus
import com.transf.kafka.messaging.common.model.request.TranscriptionMessageRequest


fun prepareProcessingResponse(
    requestId: String,
    status: MessageStatus,
    result: String
) = TranscriptionMessageRequest(
    requestId,
    result,
    status
)
