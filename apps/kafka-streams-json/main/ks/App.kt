package ks

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kstream.Kafka
import kstream.KafkaStreamsFactory
import kstream.json.JsonSerde
import kstream.json.materializedAsJson
import no.tordly.json.Vedtak
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::app).start(wait = true)
}

internal fun createTopology(): Topology =
    StreamsBuilder().apply {
        val vedtakTable = this.table<String, Vedtak>("vedtak.json", materializedAsJson("vedtak-store"))
        createSøknadStream(vedtakTable)
        createMedlemStream(vedtakTable)
    }.build()

fun Application.app(
    kafkaFactory: Kafka = KafkaStreamsFactory(defaultValueSerdeClass = JsonSerde::class.java),
    topology: Topology = createTopology(),
) {
    val kStream = kafkaFactory.createKStream(topology = topology)

    environment.monitor.subscribe(ApplicationStarted) {
        kStream.start()
    }

    environment.monitor.subscribe(ApplicationStopping) {
        kStream.close()
    }
}
