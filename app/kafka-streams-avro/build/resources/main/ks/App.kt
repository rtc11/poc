package ks

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kstream.Kafka
import kstream.KafkaStreamsFactory
import no.nav.aap.avro.Vedtak
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Materialized

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::app).start(wait = true)
}

internal fun createTopology(): Topology =
    StreamsBuilder().apply {
        val vedtakTable = table<String, Vedtak>("vedtak.avro", Materialized.`as`("vedtak-store"))
        createSøknadStream(vedtakTable)
        createMedlemStream(vedtakTable)
    }.build()

fun Application.app(
    kafkaFactory: Kafka = KafkaStreamsFactory("http://localhost:8085"),
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
