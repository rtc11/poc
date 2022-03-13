import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("vilkår.AppKt")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    runtimeOnly("ch.qos.logback:logback-classic:1.2.7")
    runtimeOnly("com.h2database:h2:2.1.210")
    implementation("io.ktor:ktor-jackson:1.6.7")
    implementation("io.ktor:ktor-serialization:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")

    implementation(project(":libs:exposed"))
    implementation(project(":libs:kafka:kafka"))
    implementation(project(":libs:kotlinx-serde"))
    implementation(project(":libs:ktor:essentials"))
    implementation(project(":libs:fp:lenses"))
    implementation(project(":libs:utils"))

    testImplementation("io.ktor:ktor-server-test-host:1.6.7")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.0")
    testImplementation(kotlin("test"))

    testImplementation(project(":libs:kafka:mock"))
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
//kotlin.sourceSets["test"].kotlin.srcDirs("test")
sourceSets["main"].resources.srcDir("main")
