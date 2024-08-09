package com.trans.service.mapping

import com.trans.domain.Test
import com.trans.dto.TestDto
import com.trans.persistanse.entity.UserEntity

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


fun UserEntity.toTest() = Test(
    id = this.id.value,
    name = this.name,
    description = this.description
)

fun UserEntity.updateFields(test: Test): UserEntity {
    this.name = test.name
    this.description = test.description
    return this
}

//fun Event.toRecord(): EventRecord = EventRecord(
//    this.description,
//    System.currentTimeMillis(),
//    this.eventName
//)
