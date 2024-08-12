
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val h2_version: String by project
val ktorm_version: String by project
val koin_Version: String by project
val ktor_version: String by project
val junit_version: String by project
val telegram_bot_api_version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.11"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

group = "com.trans"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-client-logging-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("org.apache.kafka:kafka-clients:3.4.0")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-resources-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml")

    //tg bot api
    implementation("dev.inmo:tgbotapi:$telegram_bot_api_version")

    // exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // h2
    implementation("com.h2database:h2:2.1.214")

    //postgres
    implementation("org.postgresql:postgresql:42.5.1")

    // liquibase
    implementation("org.liquibase:liquibase-core:4.15.0")

    // koin
    implementation("io.insert-koin:koin-ktor:$koin_Version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_Version")


    // testing
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("io.insert-koin:koin-test:$koin_Version")
    testImplementation("io.insert-koin:koin-test-junit5:$koin_Version")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

}
