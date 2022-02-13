package vilkår.søknad

import kotlinx.coroutines.CoroutineDispatcher
import no.nav.aap.kafka.Kafka
import no.nav.aap.kafka.createConsumer
import no.nav.aap.kafka.extensions.sendAndLog
import no.nav.aap.kafka.extensions.toProducerRecord
import no.nav.aap.kafka.flow.KafkaMessageFlow
import no.nav.aap.kafka.models.KSøknad
import no.nav.aap.kafka.models.søknadIdLens
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import vilkår.Avklaringspenger
import vilkår.Config
import vilkår.Database
import java.lang.invoke.MethodHandles
import java.util.*

class SøknadFlow(
    kafka: Kafka,
    config: Config,
    context: CoroutineDispatcher,
) : AutoCloseable, KafkaMessageFlow<KSøknad>(
    context = context,
    topic = config.søknad,
    consumer = kafka.createConsumer(config.søknad)
) {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private val producer = kafka.createProducer<KSøknad>(config.søknad)

    override suspend fun filter(record: ConsumerRecord<String, KSøknad>): Boolean {
        return Database.getSøker(record.value().personident) == null && record.value().id == null
    }

    override suspend fun action(record: ConsumerRecord<String, KSøknad>) {
        log.trace("Søknad received")

        val id = UUID.randomUUID()
        Database.save(Avklaringspenger(record.value().personident, id))

        val producerRecordWithNewId = record.toProducerRecord {
            søknadIdLens(this) { id }
        }

        producer.sendAndLog(producerRecordWithNewId, log)
    }

    override fun close() {
        producer.close()
    }
}
