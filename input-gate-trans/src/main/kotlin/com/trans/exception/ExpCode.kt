package com.trans.exception

import io.ktor.http.*

enum class ExpCode(status: HttpStatusCode) {

    DATABASE_ERROR(HttpStatusCode.NotFound),
    MAPPING_ERROR(HttpStatusCode.NotFound),
    NOT_FOUND (HttpStatusCode.NotFound),
    VALIDATION_FAILURE(HttpStatusCode.BadRequest),
    ACCOUNT_NOT_FOUND(HttpStatusCode.NotFound),
    ACCOUNT_ALREADY_EXIST(HttpStatusCode.NotFound),
    PASSWORD_ERROR(HttpStatusCode.NotFound),
    NOT_ALLOWED(HttpStatusCode.NotFound),
    TRANSACTION_REQUEST_INVALID(HttpStatusCode.NotFound),
    TRANSACTION_NOT_FOUND(HttpStatusCode.NotFound),
    ADMINISTRATOR_NOT_FOUND(HttpStatusCode.NotFound)

}
