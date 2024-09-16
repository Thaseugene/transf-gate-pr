package storage.trans.com.configuration

import com.zaxxer.hikari.HikariDataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.FileSystemResourceAccessor
import java.io.File

fun invokeMigrationProcess(datasource: HikariDataSource) {
    val database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(JdbcConnection(datasource.connection))

    val liquibase = Liquibase(
        "/db/changelog/master.xml",
        FileSystemResourceAccessor(File("transf-storage/src/main/resources")),
        database
    )

    liquibase.update("")
    liquibase.close()
}
