package com.trans.telegram.model

data class CachedResponse(
    val userId: Long,
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    val timeStamp: Long,
    var transcriptResult: String? = null,
    var translations: List<Pair<String, String>> = mutableListOf()
)
