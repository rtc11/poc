package ks

import kstream.json.JsonSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

class KStreamMock(topology: Topology) : AutoCloseable {
    val driver = TopologyTestDriver(topology, defaultProperties())

    inline fun <reified V : Any> createJsonInputTopic(name: String): TestInputTopic<String, V> =
        driver.createInputTopic(name, Serdes.StringSerde().serializer(), JsonSerde(V::class.java).serializer())

    inline fun <reified V : Any> createJsonOutputTopic(name: String): TestOutputTopic<String, V> =
        driver.createOutputTopic(name, Serdes.StringSerde().deserializer(), JsonSerde(V::class.java).deserializer())

    inline fun <reified K : Any, reified V : Any> getKeyValueStore(storeName: String): KeyValueStore<K, V> =
        driver.getKeyValueStore(storeName)

    override fun close() {
        driver.close()
    }

    private fun defaultProperties() = Properties().apply {
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "")
        put(StreamsConfig.APPLICATION_ID_CONFIG, "topology.test.driver.${UUID.randomUUID()}")
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java)
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde::class.java)
        put(StreamsConfig.STATE_DIR_CONFIG, "build/kafka-streams/state")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        put(ProducerConfig.ACKS_CONFIG, "all")
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