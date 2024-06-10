package com.trans.service.mapping

import com.trans.domain.Test
import com.trans.dto.TestDto

fun TestDto.toTest(): Test = Test(
    this.id,
    this.name,
    this.description
)

fun Test.toResponse(): TestDto = TestDto(
    this.id,
    this.name,
    this.description
)
