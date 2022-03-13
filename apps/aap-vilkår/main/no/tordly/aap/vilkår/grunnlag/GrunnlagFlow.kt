package no.tordly.aap.vilkår.grunnlag

import kotlinx.coroutines.CoroutineDispatcher
import no.tordly.kafka.Kafka
import no.tordly.kafka.createConsumer
import no.tordly.kafka.extensions.sendAndLog
import no.tordly.kafka.extensions.toProducerRecord
import no.tordly.kafka.flow.KafkaMessageFlow
import no.tordly.aap.vilkår.models.KGrunnlag
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.Producer
import org.slf4j.LoggerFactory
import no.tordly.aap.vilkår.Config
import no.tordly.aap.vilkår.Database
import java.lang.invoke.MethodHandles

interface GrunnlagStrategy {
    suspend fun applicable(value: KGrunnlag): Boolean
    suspend fun execute(value: KGrunnlag)
}

class GrunnlagFlow(
    kafka: Kafka,
    config: Config,
    context: CoroutineDispatcher,
) : AutoCloseable, KafkaMessageFlow<KGrunnlag>(
    context = context,
    topic = config.grunnlag,
    consumer = kafka.createConsumer(config.grunnlag)
) {
    private val producer: Producer<String, KGrunnlag> = kafka.createProducer(config.grunnlag)
    private val strategies = listOf(AlderStrategy, ArbeidsevneStrategy)
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    override suspend fun filter(record: ConsumerRecord<String, KGrunnlag>): Boolean {
        return Database.hasSøker(record.value().personident)
    }

    override suspend fun action(record: ConsumerRecord<String, KGrunnlag>) {
        log.trace("Grunnlag received")

        strategies.filter { it.applicable(record.value()) }
            .onEach { it.execute(record.value()) }
            .also { if (it.isEmpty()) putBackOnTopic(record) }
    }

    private fun putBackOnTopic(record: ConsumerRecord<String, KGrunnlag>) {
        producer.sendAndLog(record.toProducerRecord())
    }

    override fun close() {
        producer.close()
    }
}
