package storage.trans.com.persistance

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import storage.trans.com.model.UserModel
import storage.trans.com.persistance.entity.UserEntity
import storage.trans.com.persistance.entity.UserTable
import storage.trans.com.service.mapping.toUserModel

interface UserRepository {

    fun save(userModel: UserModel): UserModel
    fun checkIsUserPresented(userId: Long): Boolean

}

class UserRepositoryImpl : UserRepository {

    override fun save(userModel: UserModel): UserModel = transaction {
        val userEntity = UserEntity.new {
            userId = userModel.userId
            userName = userModel.userName
            firstName = userModel.firstName
            lastName = userModel.lastName
        }
        userModel.copy(
            id = userEntity.id.value,
            userId = userEntity.userId,
            userName = userEntity.userName,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName
        )
    }

    override fun checkIsUserPresented(userId: Long): Boolean = transaction {
        findBy(UserTable.userId, userId).filterNotNull().map { it.toUserModel() }.isEmpty()
    }

    private fun findExistingById(id: Long): UserEntity? = UserEntity.findById(id)

    private fun <T> findBy(column: Column<T>, value: T): List<UserEntity?> = transaction {
        UserTable.select { column eq value }
            .mapNotNull { toUserEntity(it) }
            .toList()
    }

    private fun toUserEntity(row: ResultRow): UserEntity {
        return UserEntity.wrap(row[UserTable.id], row)
    }

}
