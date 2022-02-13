package no.nav.aap.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

interface Kafka {
    fun <V : Any> createConsumer(topic: TopicConfig, clazz: Class<V>): Consumer<String, V>
    fun <V : Any> createProducer(topic: TopicConfig): Producer<String, V>
}

inline fun <reified V : Any> Kafka.createConsumer(topic: TopicConfig): Consumer<String, V> {
    return createConsumer(topic, V::class.java)
}

class KafkaFactory(private val config: KafkaConfig) : Kafka {

    override fun <V : Any> createConsumer(topic: TopicConfig, clazz: Class<V>): Consumer<String, V> {
        val properties = topic.consumerProperties() + aivenProperties()
        return KafkaConsumer(properties, StringDeserializer(), JsonDeserializer(clazz))
    }

    override fun <V : Any> createProducer(topic: TopicConfig): Producer<String, V> {
        val properties = topic.producerProperties() + aivenProperties()
        return KafkaProducer(properties, StringSerializer(), JsonSerializer<V>())
    }

    private fun TopicConfig.consumerProperties() = mapOf(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
        CommonClientConfigs.CLIENT_ID_CONFIG to consumerClientId,
        ConsumerConfig.GROUP_ID_CONFIG to groupId,
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
    )

    private fun TopicConfig.producerProperties() = mapOf(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
        CommonClientConfigs.CLIENT_ID_CONFIG to producerClientId,
        ProducerConfig.ACKS_CONFIG to "all",
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
    )

    private fun aivenProperties() = when (config.security) {
        false -> mapOf()
        true -> mapOf(
            CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SSL",
            SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG to "JKS",
            SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to config.truststorePath,
            SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to config.credstorePsw,
            SslConfigs.SSL_KEYSTORE_TYPE_CONFIG to "PKCS12",
            SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to config.keystorePath,
            SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to config.credstorePsw,
            SslConfigs.SSL_KEY_PASSWORD_CONFIG to config.credstorePsw,
            SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG to "",
        )
    }
}
