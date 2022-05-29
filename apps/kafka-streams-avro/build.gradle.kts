plugins {
    application
}

application {
    mainClass.set("ks.AppKt")
}

dependencies {
    runtimeOnly("ch.qos.logback:logback-classic:1.2.7")

    implementation("io.ktor:ktor-server-netty:1.6.7")

    implementation(project(":contracts:aap-avro"))
    implementation(project(":libs:kafka:streams"))

    testImplementation(kotlin("test"))
    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.0.0")
}
