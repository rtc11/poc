package no.nav.aap.kafka.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import no.nav.aap.kafka.TopicConfig
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.time.Duration

/**
 * Streams messages from a kafka topic into a kotlin flow
 */
abstract class KafkaMessageFlow<V : Any>(
    private val topic: TopicConfig,
    private val consumer: Consumer<String, V>,
    context: CoroutineDispatcher = Dispatchers.Default,
    private val timeout: Duration = Duration.ofMillis(10),
) : AutoCloseable {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    abstract suspend fun filter(record: ConsumerRecord<String, V>): Boolean
    abstract suspend fun action(record: ConsumerRecord<String, V>)

    private fun poll(): Flow<ConsumerRecord<String, V>> = flow {
        try {
            consumer.subscribe(listOf(topic.name))

            while (currentCoroutineContext().isActive)
                consumer
                    .poll(timeout)
                    .onEach { log.trace("Consumed[${topic.name}]=${it.value()}") }
                    .filter { it.value() != null }
                    .forEach { emit(it) }

        } finally {
            consumer.unsubscribe()
        }
    }

    private val job = CoroutineScope(context).launch {
        while (isActive)
            runCatching {
                poll().filter(::filter).collect(::action)
            }.onFailure {
                log.error("Error while reading topic ${topic.name}", it)
                if (it is CancellationException) throw it
                delay(5_000)
            }
    }

    override fun close() {
        if (!job.isCompleted) runBlocking {
            job.cancelAndJoin()
        }
        consumer.close()
    }
}