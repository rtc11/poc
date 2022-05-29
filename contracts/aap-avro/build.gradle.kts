import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask
import com.github.davidmc24.gradle.plugin.avro.GenerateAvroProtocolTask
import com.github.davidmc24.gradle.plugin.avro.GenerateAvroSchemaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.davidmc24.gradle.plugin.avro") version "1.3.0"
}

tasks {
    val generateProtocol = task("generateProtocol", GenerateAvroProtocolTask::class) {
        source("main/no/tordly/avro/aap")
        setOutputDir(file("$buildDir/generated/avpr"))
    }

    val generateSchema = task("generateSchema", GenerateAvroSchemaTask::class) {
        dependsOn(generateProtocol)
        source("$buildDir/generated/avpr")
        setOutputDir(file("$buildDir/generated/avsc"))
    }

    val generateAvro = task("generateAvro", GenerateAvroJavaTask::class) {
        dependsOn(generateSchema)
        source("$buildDir/generated/avsc")
        setOutputDir(file("$buildDir/generated/avro"))
    }

    withType<KotlinCompile> {
        source(generateAvro)
    }
}

dependencies {
    api("org.apache.avro:avro:1.11.0")
}

sourceSets["main"].java.srcDirs("build/generated/avro")
