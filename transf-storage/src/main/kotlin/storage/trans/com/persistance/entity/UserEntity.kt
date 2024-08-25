package storage.trans.com.persistance.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

const val DEFAULT_VARCHAR_COLUMN_LENGTH = 255

class UserEntity(id: EntityID<Long>): LongEntity(id) {

    var userId by UserTable.userId
    var userName by UserTable.userName
    var firstName by UserTable.firstName
    var lastName by UserTable.lastName

    companion object : LongEntityClass<UserEntity>(UserTable)

}

object UserTable: LongIdTable("USERS", "ID") {

    val userId = long("USER_ID")
    val userName = varchar("USER_NAME", DEFAULT_VARCHAR_COLUMN_LENGTH).nullable()
    val firstName = varchar("FIRST_NAME", DEFAULT_VARCHAR_COLUMN_LENGTH).nullable()
    val lastName = varchar("LAST_NAME", DEFAULT_VARCHAR_COLUMN_LENGTH).nullable()

}
