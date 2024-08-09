package com.trans.persistanse

import com.trans.domain.Test
import com.trans.exception.ExpCode
import com.trans.exception.RepositoryException
import com.trans.persistanse.entity.UserEntity
import com.trans.service.mapping.toTest
import com.trans.service.mapping.updateFields
import org.jetbrains.exposed.sql.transactions.transaction

interface TestRepository {

    fun save(test: Test): Test
    fun delete(id: Long)
    fun update(test: Test): Test
    fun findById(id: Long): Test
    fun findAll(): List<Test>

}

class TestRepositoryImpl : TestRepository {

    override fun save(test: Test): Test = transaction {
        val createdEntity = UserEntity.new {
            name = test.name
            description = test.description
        }
        test.copy(
            id = createdEntity.id.value,
            description = createdEntity.description
        )
    }

    override fun delete(id: Long) = transaction {
        val existingEntity = findExistingById(id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Test with id = $id doesn't exist")
        existingEntity.delete()
    }

    override fun update(test: Test): Test = transaction {
        val existingEntity = findExistingById(test.id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Test with id = $id doesn't exist")
        existingEntity.updateFields(test).toTest()
    }

    override fun findById(id: Long): Test = transaction {
        findExistingById(id)?.toTest() ?: throw RepositoryException(ExpCode.NOT_FOUND, "Test with id = $id doesn't exist")
    }

    override fun findAll(): List<Test> = transaction {
        UserEntity.all().map { it.toTest() }
    }

    private fun findExistingById(id: Long): UserEntity? = UserEntity.findById(id)

}
