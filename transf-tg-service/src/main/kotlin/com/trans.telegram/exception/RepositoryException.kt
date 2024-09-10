package com.trans.telegram.exception

class RepositoryException(expCode: ExpCode, message: String): InnerException(expCode, message)
