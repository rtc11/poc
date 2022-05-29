dependencies {
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    api("org.apache.kafka:kafka-clients:3.0.0")
    api("org.apache.kafka:kafka-streams:3.0.0")
    api("io.confluent:kafka-streams-avro-serde:7.0.1") {
        exclude("org.apache.kafka", "kafka-clients")
    }
}
