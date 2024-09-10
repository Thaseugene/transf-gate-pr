package com.trans.telegram.utils

import com.trans.telegram.exception.ExpCode
import com.trans.telegram.exception.ValidationException

fun String?.extractId(): Long =
    this?.toLong() ?:
    throw ValidationException(ExpCode.VALIDATION_FAILURE, "Incorrect format for id")
