package kstream.json

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier
import org.apache.kafka.streams.state.KeyValueStore

inline fun <reified V : Any> consumedWithJson(named: String): Consumed<String, V> =
    Consumed.with(Serdes.StringSerde(), JsonSerde(V::class.java))
        .withName(named)

inline fun <reified V : Any> consumedAsJson(named: String): Consumed<String, V> =
    Consumed.`as`<String?, V>(named)
        .withKeySerde(Serdes.StringSerde())
        .withValueSerde(JsonSerde(V::class.java))

inline fun <reified V : Any> producedWithJson(named: String): Produced<String, V> =
    Produced.with(Serdes.StringSerde(), JsonSerde(V::class.java))
        .withName(named)

inline fun <reified V : Any, reified VO : Any> joinedWithJson(named: String): Joined<String, V, VO> =
    Joined.with(
        Serdes.StringSerde(),
        JsonSerde(V::class.java),
        JsonSerde(VO::class.java),
        named,
    )

inline fun <reified V : Any, reified VO : Any> joinedAsJson(name: String): Joined<String, V, VO> =
    Joined.`as`<String, V, VO>(name)
        .withKeySerde(Serdes.StringSerde())
        .withValueSerde(JsonSerde(V::class.java))
        .withOtherValueSerde(JsonSerde(VO::class.java))

inline fun <reified V : Any> materializedWithJson(): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> =
    Materialized.with(Serdes.StringSerde(), JsonSerde(V::class.java))

inline fun <reified V : Any> materializedAsJson(supplier: KeyValueBytesStoreSupplier): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> =
    Materialized.`as`(supplier)

inline fun <reified V : Any> materializedAsJson(name: String): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> =
    Materialized.`as`<String, V, KeyValueStore<Bytes, ByteArray>>(name)
        .withKeySerde(Serdes.StringSerde())
        .withValueSerde(JsonSerde(V::class.java))

inline fun <reified V : Any> groupedWithJson(): Grouped<String, V> =
    Grouped.with(Serdes.StringSerde(), JsonSerde(V::class.java))
