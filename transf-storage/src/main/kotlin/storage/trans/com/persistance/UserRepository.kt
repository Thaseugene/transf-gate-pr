package storage.trans.com.persistance

import storage.trans.com.exception.ExpCode
import com.trans.exception.RepositoryException
import com.trans.service.mapping.toUserModel
import com.trans.service.mapping.updateFields
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import storage.trans.com.domain.UserModel
import storage.trans.com.persistance.entity.UserEntity
import storage.trans.com.persistance.entity.UserTable

interface UserRepository {

    fun save(userModel: UserModel): UserModel
    fun delete(id: Long)
    fun update(userModel: UserModel): UserModel
    fun findById(id: Long): UserModel
    fun findByUserId(userId: Long): UserModel
    fun findAll(): List<UserModel>
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

    override fun delete(id: Long) = transaction {
        val existingEntity = findExistingById(id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "User with id = $id doesn't exist")
        existingEntity.delete()
    }

    override fun update(userModel: UserModel): UserModel = transaction {
        val existingEntity = findExistingById(userModel.userId) ?: throw RepositoryException(ExpCode.NOT_FOUND, "User with id = $id doesn't exist")
        existingEntity.updateFields(userModel).toUserModel()
    }

    override fun findById(id: Long): UserModel = transaction {
        findExistingById(id)?.toUserModel() ?: throw RepositoryException(ExpCode.NOT_FOUND, "User with id = $id doesn't exist")
    }

    override fun findAll(): List<UserModel> = transaction {
        UserEntity.all().map { it.toUserModel() }
    }

    override fun checkIsUserPresented(userId: Long): Boolean = transaction {
        findBy(UserTable.userId, userId).filterNotNull().map { it.toUserModel() }.isEmpty()
    }

    override fun findByUserId(userId: Long): UserModel = transaction {
        val userList = findBy(UserTable.userId, userId).filterNotNull().map { it.toUserModel() }
        if (userList.isEmpty()) {
            throw RepositoryException(ExpCode.NOT_FOUND, "User with userId = $userId doesn't exist")
        }
        userList.first()
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
