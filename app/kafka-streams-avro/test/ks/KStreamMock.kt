package ks

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

private val MOCK_SCHEMA_REG = "mock://test-${UUID.randomUUID()}"
val AVRO_SERDE_CONFIG = mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to MOCK_SCHEMA_REG)

class KStreamMock(topology: Topology) : AutoCloseable {
    val driver = TopologyTestDriver(topology, defaultProperties())

    inline fun <reified V : SpecificRecord> createInputTopic(name: String): TestInputTopic<String, V> {
        val avroSerde = SpecificAvroSerde<V>().apply { configure(AVRO_SERDE_CONFIG, false) }
        return driver.createInputTopic(name, Serdes.StringSerde().serializer(), avroSerde.serializer())
    }

    inline fun <reified V : SpecificRecord> createOutputTopic(name: String): TestOutputTopic<String, V> {
        val avroSerde = SpecificAvroSerde<V>().apply { configure(AVRO_SERDE_CONFIG, false) }
        return driver.createOutputTopic(name, Serdes.StringSerde().deserializer(), avroSerde.deserializer())
    }

    inline fun <reified K : Any, reified V : Any> getKeyValueStore(storeName: String): KeyValueStore<K, V> =
        driver.getKeyValueStore(storeName)

    override fun close() = driver.close()

    private fun defaultProperties() = Properties().apply {
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "")
        put(StreamsConfig.APPLICATION_ID_CONFIG, "topology.test.driver.${UUID.randomUUID()}")
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java)
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java)
        put(StreamsConfig.STATE_DIR_CONFIG, "build/kafka-streams/avro-state")
        put(StreamsConfig.MAX_TASK_IDLE_MS_CONFIG, StreamsConfig.MAX_TASK_IDLE_MS_DISABLED)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        put(ProducerConfig.ACKS_CONFIG, "all")
        put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REG)
    }
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) {
    pipeInput(key, value())
}

fun <T> KeyValueStore<String, T>.getAllValues(): List<T> {
    val it = this.all()
    val values = it.asSequence().map { it.value }.toList()
    it.close() // Close the iterator
    return values
}

fun <T> KeyValueStore<String, T>.getAll(): Map<String, T> {
    val it = this.all()
    val values = it.asSequence().map { it.key to it.value }.toMap()
    it.close() // Close the iterator
    return values
}