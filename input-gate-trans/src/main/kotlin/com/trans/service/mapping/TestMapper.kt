package com.trans.service.mapping

import com.trans.domain.Event
import com.trans.domain.Test
import com.trans.dto.EventRecord
import com.trans.dto.TestDto
import com.trans.persistanse.TestEntity

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


fun TestEntity.toTest() = Test(
    id = this.id.value,
    name = this.name,
    description = this.description
)

fun TestEntity.updateFields(test: Test): TestEntity {
    this.name = test.name
    this.description = test.description
    return this
}

//fun Event.toRecord(): EventRecord = EventRecord(
//    this.description,
//    System.currentTimeMillis(),
//    this.eventName
//)
