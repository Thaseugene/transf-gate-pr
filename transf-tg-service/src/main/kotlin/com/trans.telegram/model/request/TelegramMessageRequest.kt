package com.transf.kafka.messaging.common.model.request

import com.trans.telegram.model.MessageStatus
import kotlinx.serialization.Serializable

@Serializable
data class TelegramMessageRequest(
    val executionStrategy: CommandStrategy,
    val userId: Long,
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    val timeStamp: Long,
    val messageValue: ByteArray,
    val lang: String? = null,
    val status: MessageStatus? = null,
    val userName: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
)

enum class CommandStrategy {
    TRANSLATION,
    TRANSCRIPTION
}
