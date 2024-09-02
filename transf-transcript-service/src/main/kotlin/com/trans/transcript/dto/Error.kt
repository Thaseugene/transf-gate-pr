package com.trans.transcript.dto

data class Error(
    val errorCode: Int,
    val errorMessage: String?,
    val timeStamp: Long = System.currentTimeMillis()
)
