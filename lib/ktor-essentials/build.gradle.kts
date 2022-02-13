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
    api("com.sksamuel.hoplite:hoplite-yaml:1.4.16")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.7")
    api("io.ktor:ktor-server-core:1.6.7")
}
