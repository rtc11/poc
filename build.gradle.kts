import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21" apply false
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://packages.confluent.io/maven/")
    }
}

subprojects {
    subprojects {
        apply(plugin = "org.jetbrains.kotlin.jvm")

        tasks {
            withType<KotlinCompile> {
                kotlinOptions.jvmTarget = "18"
            }

            withType<Test> {
                useJUnitPlatform()
            }
        }

        configurations.all {
            resolutionStrategy {
                force(
                    "org.apache.kafka:kafka-clients:3.2.0"
                )
            }
        }

        kotlin.sourceSets["main"].kotlin.srcDirs("main")
        kotlin.sourceSets["test"].kotlin.srcDirs("test")
    }
}
