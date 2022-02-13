import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

dependencies {
    api(project(":lib:ktor-essentials"))
    api(project(":lib:kotlinx-serde"))

    api("org.apache.kafka:kafka-clients:2.8.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    api("org.slf4j:slf4j-api:1.7.32")
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
