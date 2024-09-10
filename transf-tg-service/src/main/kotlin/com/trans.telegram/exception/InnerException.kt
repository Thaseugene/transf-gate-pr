package com.trans.telegram.exception

open class InnerException(val expCode: ExpCode, message: String): RuntimeException(message) {

    val timeStamp: Long = System.currentTimeMillis()

}
