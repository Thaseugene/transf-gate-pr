package com.trans.persistanse

import com.trans.domain.UserModel
import com.trans.exception.ExpCode
import com.trans.exception.RepositoryException
import com.trans.persistanse.entity.UserEntity
import com.trans.service.mapping.toNewEntity
import com.trans.service.mapping.toUserModel
import com.trans.service.mapping.updateFields
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {

    fun save(userModel: UserModel): UserModel
    fun delete(id: Long)
    fun update(userModel: UserModel): UserModel
    fun findById(id: Long): UserModel
    fun findAll(): List<UserModel>

}

class UserRepositoryImpl : UserRepository {

    override fun save(userModel: UserModel): UserModel = transaction {
        val userEntity = userModel.toNewEntity()
        userModel.copy(
            userId = userEntity.id.value,
            userName = userEntity.userName,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName
        )
    }

    override fun delete(id: Long) = transaction {
        val existingEntity = findExistingById(id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Test with id = $id doesn't exist")
        existingEntity.delete()
    }

    override fun update(userModel: UserModel): UserModel = transaction {
        val existingEntity = findExistingById(userModel.userId) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Test with id = $id doesn't exist")
        existingEntity.updateFields(userModel).toUserModel()
    }

    override fun findById(id: Long): UserModel = transaction {
        findExistingById(id)?.toUserModel() ?: throw RepositoryException(ExpCode.NOT_FOUND, "Test with id = $id doesn't exist")
    }

    override fun findAll(): List<UserModel> = transaction {
        UserEntity.all().map { it.toUserModel() }
    }

    private fun findExistingById(id: Long): UserEntity? = UserEntity.findById(id)

}
