package no.nav.aap.kafka.extensions

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

fun <K, V : Any> ConsumerRecord<K, V>.toProducerRecord(updatedValue: V.() -> V = { value() }): ProducerRecord<K, V> {
    return ProducerRecord(topic(), partition(), key(), updatedValue(value()), listOf())
}
