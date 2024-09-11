package com.trans.telegram.model.request

import com.trans.telegram.model.MessageStatus
import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageRequest(
    val userId: Long,
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    val timeStamp: Long,
    val messageValue: ByteArray,
    val messageResult: ByteArray? = null,
    val lang: String? = null,
    val status: MessageStatus? = null,
    val userName: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
)
