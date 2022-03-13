package no.tordly.kafka

data class KafkaConfig(
    val brokers: String,
    val security: Boolean,
    val truststorePath: String,
    val keystorePath: String,
    val credstorePsw: String,
)

data class TopicConfig(
    val name: String,
    val groupId: String,
    private val clientId: String,
) {
    val producerClientId: String get() = "$clientId-producer"
    val consumerClientId: String get() = "$clientId-consumer"
}
