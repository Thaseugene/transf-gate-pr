package com.trans.transcript.service.mapping

import com.trans.transcript.model.MessageStatus
import com.trans.transcript.model.response.ProcessingMessageResponse


fun prepareProcessingResponse(requestId: String, status: MessageStatus, result: String) = ProcessingMessageResponse(
    requestId,
    result,
    status
)
