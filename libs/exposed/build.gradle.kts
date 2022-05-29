plugins {
    `java-library`
}

dependencies {
    api("org.flywaydb:flyway-core:8.4.3")
    api("org.jetbrains.exposed:exposed-java-time:0.37.3")
    api("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    api("org.jetbrains.exposed:exposed-dao:0.37.3")
}
