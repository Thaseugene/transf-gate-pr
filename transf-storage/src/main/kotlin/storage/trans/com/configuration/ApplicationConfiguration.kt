package storage.trans.com.configuration

import com.transf.kafka.messaging.configuration.ConsumerInnerConfig
import com.transf.kafka.messaging.configuration.KafkaInnerConfig
import com.transf.kafka.messaging.configuration.ProducerInnerConfig
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
    val bootstrapServers = kafkaConfigObject.property("bootstrapServers").getList()
    val groupId = kafkaConfigObject.property("groupId").getString()
    val producerTopics = kafkaConfigObject.configList("producerTopics").associate {
        val producerInnerConfig = ProducerInnerConfig(
            it.property("name").getString(),
            it.property("senderType").getString()
        )
        producerInnerConfig.topicName to producerInnerConfig
    }
    val consumerTopics = kafkaConfigObject.configList("consumerTopics").associate {
        val consumerInnerConfig = ConsumerInnerConfig(
            it.property("name").getString(),
            it.property("handler").getString()
        )
        consumerInnerConfig.topicName to consumerInnerConfig
    }

    appConfig.kafkaConfig = KafkaInnerConfig(groupId, bootstrapServers, consumerTopics, producerTopics)
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
