package com.trans.utils

import com.trans.exception.ExpCode
import com.trans.exception.ValidationException

fun String?.extractId(): Long =
    this?.toLong() ?:
    throw ValidationException(ExpCode.VALIDATION_FAILURE, "Incorrect format for id")
