package com.trans.configuration

import io.ktor.server.application.*

class ApplicationConfiguration {
    lateinit var databaseConfig: DatabaseConfig
    lateinit var kafkaConfig: KafkaInnerConfig
}

fun Application.configureApplication(): ApplicationConfiguration {
    val appConfig = ApplicationConfiguration()

    //database config import
    val databaseConfigObject = environment.config.config("database")
    val driverClass = databaseConfigObject.property("driverClass").getString()
    val url = databaseConfigObject.property("url").getString()
    val user = databaseConfigObject.property("user").getString()
    val password = databaseConfigObject.property("password").getString()
    val maxPoolSize = databaseConfigObject.property("maxPoolSize").getString().toInt()

    //kafka config import
    val kafkaConfigObject = environment.config.config("kafka")
    val registryUrl = kafkaConfigObject.property("registryUrl").getString()
    val bootstrapServers = kafkaConfigObject.property("bootstrapServers").getList()
    val topicNames =  kafkaConfigObject.property("topics").getList()

    appConfig.kafkaConfig = KafkaInnerConfig(registryUrl, bootstrapServers, topicNames)
    appConfig.databaseConfig = DatabaseConfig(driverClass, url, user, password, maxPoolSize)
    return appConfig
}

data class DatabaseConfig(
    val driverClass: String,
    val url: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int
)

data class KafkaInnerConfig(
    val registryUrl: String,
    val bootstrapServers: List<String>,
    val topicName: List<String>
)
