package kstream

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer
import kstream.json.JsonDeserializer
import kstream.json.JsonSerializer
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.*
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.errors.InvalidStateStoreException
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*
import kotlin.reflect.full.isSubclassOf

interface Kafka {
    fun createKStream(topology: Topology): KafkaStreams
}

class KafkaStreamsFactory(
    private val schemaRegistryUrl: String? = null,
    private val defaultKeySerdeClass: Class<*> = Serdes.StringSerde::class.java,
    private val defaultValueSerdeClass: Class<*> = SpecificAvroSerde::class.java,
) : Kafka {
    override fun createKStream(topology: Topology): KafkaStreams {
        val properties = streamsProperties<Any>(
            schemaRegistryUrl,
            defaultKeySerdeClass,
            defaultValueSerdeClass,
        )

        return KafkaStreams(topology, properties)
    }
}

private inline fun <reified V : Any> streamsProperties(
    schemaRegUrl: String?,
    keySerde: Class<*>,
    valueSerde: Class<*>,
): Properties = Properties().apply {
    consumerProperties<V>()
    producerProperties<V>()

    put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, keySerde)
    put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, valueSerde)
    put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-example")
    put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
    put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, LogAndSkipOnInvalidTimestamp::class.java)
    put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1)
    put(StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG), "all")
    put(StreamsConfig.STATE_DIR_CONFIG, "build/kafka-streams/state") // TODO: only use this when running locally
    put(StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE)

    if (schemaRegUrl != null) put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegUrl)
}

private inline fun <reified V : Any> Properties.consumerProperties() {
    val deserializer: Deserializer<out Any> = when (V::class.isSubclassOf(SpecificRecord::class)) {
        true -> SpecificAvroDeserializer()
        false -> JsonDeserializer(V::class.java)
    }

    put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-streams-example")
    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer::class.java.name)
}

private inline fun <reified V : Any> Properties.producerProperties() {
    val serializer: Serializer<out Any> = when ((V::class.isSubclassOf(SpecificRecord::class))) {
        true -> SpecificAvroSerializer()
        false -> JsonSerializer()
    }

    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer::class.java.name)
}

fun <K, V> KafkaStreams.waitForStore(storeName: String): ReadOnlyKeyValueStore<K, V> {
    var stateStore: ReadOnlyKeyValueStore<Any, Any>? = null
    val timestamp = System.currentTimeMillis()
    val storeWaitTimeoutMillis = 10000

    while (stateStore == null && System.currentTimeMillis() < timestamp + storeWaitTimeoutMillis) {
        try {
            stateStore = this.store(
                StoreQueryParameters.fromNameAndType<ReadOnlyKeyValueStore<Any, Any>>(
                    storeName, QueryableStoreTypes.keyValueStore()
                )
            )
        } catch (ignored: InvalidStateStoreException) {
            Thread.sleep(500) // store not yet ready for querying
        }
    }

    @Suppress("UNCHECKED_CAST") return when (stateStore != null) {
        true -> stateStore as ReadOnlyKeyValueStore<K, V>
        else -> error("Store $storeName did not become available for querying within $storeWaitTimeoutMillis ms")
    }
}
