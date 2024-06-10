package com.trans.persistanse

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

const val DEFAULT_VARCHAR_COLUMN_LENGTH = 255

class TestEntity(id: EntityID<Long>): LongEntity(id) {
    var name by TestTable.name
    var description by TestTable.description

    companion object : LongEntityClass<TestEntity>(TestTable)
}

object TestTable: LongIdTable("TEST", "ID") {
    val name = varchar("NAME", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val description = varchar("DESCRIPTION", DEFAULT_VARCHAR_COLUMN_LENGTH)
}
