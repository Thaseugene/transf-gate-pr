package com.trans.dto

data class EventRequest(
    val id: Long,
    val clientId: String,
    val topicName: String,
    val description: String,
    val timeStamp: Long = System.currentTimeMillis(),
    val eventName: String,
    val value: ByteArray? = null
)
