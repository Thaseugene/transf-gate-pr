package storage.trans.com.exception

import storage.trans.com.exception.ExpCode

open class InnerException(message: String): RuntimeException(message) {

    val timeStamp: Long = System.currentTimeMillis()

}
