val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project
val koin_Version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.11"
}

group = "com.trans.translate"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.cio.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {

    //server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")

    implementation(project(":kafka-messaging"))

    // kafka
    implementation("org.apache.kafka:kafka-clients:3.4.0")

    // client
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-client-logging-jvm")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")

    // koin
    implementation("io.insert-koin:koin-ktor:$koin_Version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_Version")

}
