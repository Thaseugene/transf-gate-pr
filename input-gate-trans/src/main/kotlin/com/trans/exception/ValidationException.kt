package com.trans.exception

class ValidationException(expCode: ExpCode, message: String): InnerException(expCode, message)
