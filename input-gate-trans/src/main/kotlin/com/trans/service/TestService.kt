package com.trans.service

import com.trans.dto.TestDto
import com.trans.persistanse.TestRepository
import com.trans.service.mapping.toResponse
import com.trans.service.mapping.toTest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface TestService {

    fun createTest(test: TestDto): TestDto
    fun deleteTest(id: String?)
    fun updateTest(test: TestDto): TestDto
    fun findTestById(id: String?): TestDto
    fun findAll(): List<TestDto>

}

class TestServiceImpl(
    private val testRepository: TestRepository
): TestService {
    private val logger: Logger = LoggerFactory.getLogger(TestService::class.java)

    override fun createTest(test: TestDto): TestDto {
        logger.info("Start creating process test model - $test")
        return testRepository.save(test.toTest()).toResponse()
    }

    override fun deleteTest(id: String?) {
        logger.info("Start deleting process test model with id - $id")
        testRepository.delete(id.extractId())
    }

    override fun updateTest(test: TestDto): TestDto {
        logger.info("Start updating process test model - $test")
        return testRepository.update(test.toTest()).toResponse()
    }

    override fun findTestById(id: String?): TestDto {
        return testRepository.findById(id.extractId()).toResponse()
    }

    override fun findAll(): List<TestDto> {
        logger.info("Start finding process for all test models")
        return testRepository.findAll().map { it.toResponse() }
    }

    private fun String?.extractId(): Long = this?.toLong() ?: throw RuntimeException("Incorrect format for id")

}
