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
    api(project(":libs:kafka:kafka"))
    api(kotlin("test"))
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
