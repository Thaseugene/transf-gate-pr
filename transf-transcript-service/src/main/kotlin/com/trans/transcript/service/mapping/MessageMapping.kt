package com.trans.transcript.service.mapping

import com.trans.transcript.dto.MessageStatus
import com.trans.transcript.dto.ProcessingMessageResponse


fun prepareProcessingResponse(requestId: String, status: MessageStatus, result: String) = ProcessingMessageResponse(
    requestId,
    result,
    status
)
