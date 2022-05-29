plugins {
    `java-library`
}

dependencies {
    api("com.sksamuel.hoplite:hoplite-yaml:1.4.16")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.7")
    api("io.ktor:ktor-server-core:1.6.7")
}
