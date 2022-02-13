package søknad

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import ktor.essentials.loadConfig
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.KafkaFactory
import no.nav.aap.kafka.TopicConfig
import no.nav.aap.kafka.extensions.sendAndLog
import no.nav.aap.kafka.models.KSøknad
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.random.nextLong

data class Config(
    val kafka: KafkaConfig,
    val søknad: TopicConfig,
)

fun main() {
    embeddedServer(Netty, port = 8082, module = Application::søknad).start(wait = true)
}

fun Application.søknad() {
    val config: Config = loadConfig()
    val kafka = KafkaFactory(config.kafka)
    val producer = kafka.createProducer<KSøknad>(config.søknad)
    val logger = LoggerFactory.getLogger("søknad")

    launch {
        withContext(Dispatchers.Default) {
            while (isActive) {
                val ident = Random.nextLong(10000000000..99999999999).toString()
                val søknad = KSøknad(ident)
                val record = ProducerRecord(config.søknad.name, ident, søknad)

                producer.sendAndLog(record, logger)

                delay(1000)
            }
        }
    }

    environment.monitor.subscribe(ApplicationStopping) {
        producer.close()
    }
}
