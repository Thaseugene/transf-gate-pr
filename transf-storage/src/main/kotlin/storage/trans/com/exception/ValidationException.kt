package com.trans.exception

import storage.trans.com.exception.ExpCode
import storage.trans.com.exception.InnerException

class ValidationException(expCode: ExpCode, message: String): InnerException(expCode, message)
