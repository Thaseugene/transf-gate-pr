package com.trans.persistanse

import org.jetbrains.exposed.dao.id.LongIdTable

class EventEntity {
}

object EventTable: LongIdTable("EVENT", "ID") {
    val name = varchar("NAME", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val description = varchar("DESCRIPTION", DEFAULT_VARCHAR_COLUMN_LENGTH)
}
