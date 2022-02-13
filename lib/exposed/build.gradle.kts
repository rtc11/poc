import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

dependencies {
    api("org.flywaydb:flyway-core:8.4.3")
    api("org.jetbrains.exposed:exposed-java-time:0.37.3")
    api("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    api("org.jetbrains.exposed:exposed-dao:0.37.3")
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
