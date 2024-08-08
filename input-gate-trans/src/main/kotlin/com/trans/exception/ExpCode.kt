package com.trans.exception

import io.ktor.http.*

enum class ExpCode(status: HttpStatusCode) {

    DATABASE_ERROR(HttpStatusCode.NotFound),
    MAPPING_ERROR(HttpStatusCode.NotFound),
    NOT_FOUND (HttpStatusCode.NotFound),
    VALIDATION_FAILURE(HttpStatusCode.BadRequest),
    NOT_ALLOWED(HttpStatusCode.NotFound),

}
