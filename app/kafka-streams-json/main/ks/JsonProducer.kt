package ks

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kstream.json.JsonSerde
import no.nav.aap.json.Medlem
import no.nav.aap.json.Søknad
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.random.nextLong

fun main() {
    embeddedServer(Netty, port = 8081) {

        val soknader = createProducer<Søknad>("søknad-consumer")
        val medlemskap = createProducer<Medlem>("medlem-consumer")
        var counter = 0
        val svar = listOf(Medlem.Response.Svar.JA, Medlem.Response.Svar.NEI, Medlem.Response.Svar.UAVKLART)

        launch {
            while (isActive) {
                val personident = randomIdent
                val arbeidetUtenlands = randomIdent[0].digitToInt() > 5

                soknader.produce("soknad.json", personident) {
                    Søknad(personident, arbeidetUtenlands)
                }

                delay(3_500)

                medlemskap.produce("medlem.json", personident) {
                    Medlem(personident, response = Medlem.Response(erMedlem = svar[counter++ % 3]))
                }

                delay(3_500)
            }
        }
    }.start(wait = true)
}

inline fun <reified V : Any> createProducer(clientId: String): Producer<String, V> {
    val properties = producerProperties(clientId)
    val serde = JsonSerde<V>()
    return KafkaProducer(properties, StringSerializer(), serde.serializer())
}

fun producerProperties(clientId: String) = mapOf(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerde::class.java.name,
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
