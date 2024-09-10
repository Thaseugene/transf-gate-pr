package com.trans.exception

import storage.trans.com.exception.ExpCode
import storage.trans.com.exception.InnerException

class RepositoryException(expCode: ExpCode, message: String): InnerException(message)
