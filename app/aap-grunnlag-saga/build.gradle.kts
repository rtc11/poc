import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("grunnlag.GrunnlagAppKt")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

dependencies {
    implementation("io.ktor:ktor-jackson:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")

    implementation(project(":lib:kafka"))
    implementation(project(":lib:ktor-essentials"))
    implementation(project(":lib:lenses"))
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
sourceSets["main"].resources.srcDir("main")
