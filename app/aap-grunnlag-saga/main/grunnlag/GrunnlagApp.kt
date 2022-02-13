package grunnlag

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ktor.essentials.loadConfig
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.KafkaFactory
import no.nav.aap.kafka.TopicConfig
import no.nav.aap.kafka.createConsumer
import no.nav.aap.kafka.extensions.sendAndLog
import no.nav.aap.kafka.flow.KafkaMessageFlow
import no.nav.aap.kafka.models.KGrunnlag
import no.nav.aap.kafka.models.KSøknad
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.nextInt

data class Config(
    val kafka: KafkaConfig,
    val søknad: TopicConfig,
    val grunnlag: TopicConfig,
)

fun main() {
    embeddedServer(Netty, port = 8081) {
        val config: Config = loadConfig()
        val kafka = KafkaFactory(config.kafka)
        val producer = kafka.createProducer<KGrunnlag>(config.grunnlag)
        val logger = LoggerFactory.getLogger("grunnlag")
        val consumer: Consumer<String, KSøknad> = kafka.createConsumer(config.søknad)
        val flow = object : KafkaMessageFlow<KSøknad>(config.søknad, consumer) {

            override suspend fun filter(record: ConsumerRecord<String, KSøknad>): Boolean {
                return record.value().id != null
            }

            override suspend fun action(record: ConsumerRecord<String, KSøknad>) {
                if (Random.nextInt(0, 100) <= 75) sendGrunnlagForAlder(record.value())
                if (Random.nextInt(0, 100) <= 75) sendGrunnlagForEvne(record.value())
            }

            fun sendGrunnlagForAlder(søknad: KSøknad) {
                val år = Random.nextInt(1922..2022)
                val grunnlag = KGrunnlag.createForAlder(søknad.personident, LocalDate.of(år, 1, 1))
                val record = ProducerRecord(config.grunnlag.name, grunnlag.personident, grunnlag)
                producer.sendAndLog(record, logger)
            }

            private fun sendGrunnlagForEvne(søknad: KSøknad) {
                val prosent = Random.nextInt(0..100)
                val grunnlag = KGrunnlag.createForArbeidsevne(søknad.personident, prosent)
                val record = ProducerRecord(config.grunnlag.name, grunnlag.personident, grunnlag)
                producer.sendAndLog(record, logger)
            }
        }

        environment.monitor.subscribe(ApplicationStopping) {
            flow.close()
            producer.close()
        }
    }.start(wait = true)
}