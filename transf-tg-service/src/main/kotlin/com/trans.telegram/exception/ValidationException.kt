package com.trans.telegram.exception

class ValidationException(expCode: ExpCode, message: String): InnerException(expCode, message)
