import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.3.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.0")
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
