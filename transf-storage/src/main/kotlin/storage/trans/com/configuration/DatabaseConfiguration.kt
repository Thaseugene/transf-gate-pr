package storage.trans.com.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

fun configureDatabase(configuration: ApplicationConfiguration): HikariDataSource {
    val defaultDatabaseFactory = DefaultDatabaseFactory(configuration)
    defaultDatabaseFactory.createConnectivity()
    return defaultDatabaseFactory.dataSource
}

class DefaultDatabaseFactory(configuration: ApplicationConfiguration) : DatabaseFactory {
    private val dbConfig = configuration.databaseConfig
    lateinit var dataSource: HikariDataSource

    override fun createConnectivity() {
        dataSource = hikari()
        Database.connect(dataSource)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = dbConfig.driverClass
        config.jdbcUrl = dbConfig.url
        config.username = dbConfig.user
        config.password = dbConfig.password
        config.maximumPoolSize = dbConfig.maxPoolSize
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

}
