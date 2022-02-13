package ks

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.nav.aap.avro.ErMedlem
import no.nav.aap.avro.Medlem
import no.nav.aap.avro.Response
import no.nav.aap.avro.Søknad
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.random.Random
import kotlin.random.nextLong

fun main() {
    embeddedServer(Netty, port = 8081) {

        val soknader = createProducer<Søknad>("søknad-consumer")
        val medlemskap = createProducer<Medlem>("medlem-consumer")
        var counter = 0
        val svar = listOf(ErMedlem.JA, ErMedlem.NEI, ErMedlem.UAVKLART)

        launch {
            while (isActive) {
                val personident = randomIdent
                val arbeidetUtenlands = randomIdent[0].digitToInt() > 5
                soknader.produce("soknad.avro", personident) {
                    Søknad(personident, arbeidetUtenlands)
                }

                delay(3_500)

                medlemskap.produce("medlem.avro", personident) {
                    Medlem(personident, UUID.randomUUID().toString(), null, Response(svar[counter++ % 3], null))
                }

                delay(3_500)
            }
        }

    }.start(wait = true)
}

inline fun <reified V : SpecificRecord> createProducer(clientId: String): Producer<String, V> {
    val properties = producerProperties(clientId)
    val serde = SpecificAvroSerializer<V>()
    serde.configure(mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "http://localhost:8085"), false)
    return KafkaProducer(properties, StringSerializer(), serde)
}

fun producerProperties(clientId: String) = mapOf(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to SpecificAvroSerializer::class.java.name,
    CommonClientConfigs.CLIENT_ID_CONFIG to clientId,
    ProducerConfig.ACKS_CONFIG to "all",
    ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
)

inline fun <reified V : Any> Producer<String, V>.produce(topic: String, key: String, value: () -> V) {
    val record = ProducerRecord(topic, key, value())
    send(record).get().also {
        LoggerFactory.getLogger("app").info("produced: ${it.topic()} $record")
    }
}

private val randomIdent: String get() = Random.nextLong(10_000_000_000 until 99_999_999_999).toString()
