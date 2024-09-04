package com.trans.exception

class RepositoryException(expCode: ExpCode, message: String): InnerException(expCode, message)
