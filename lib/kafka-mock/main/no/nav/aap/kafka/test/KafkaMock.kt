package no.nav.aap.kafka.test

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.aap.kafka.Kafka
import no.nav.aap.kafka.TopicConfig
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("UNCHECKED_CAST")
/**
 * @param timeoutMs is a graceful timeout to wait before shutting down the test application instance.
 * This is necessary when we e.g. wait for some asynchronous operations to complete.
 */
class KafkaMock(
    private val timeoutMs: Long, private val log: Logger = LoggerFactory.getLogger("kafka")
) : Kafka {
    private val topics = mutableMapOf<String, TestTopic<String, *>>()

    fun <V> consume(topic: String, offset: Int = 0): List<ConsumerRecord<String, V>> {
        return getOrCreateTopic<V>(topic).consume(offset).map { (key, value) ->
            ConsumerRecord(topic, 0, 0L, key, value)
        }
    }

    fun <V> produce(topic: String, key: String = "${UUID.randomUUID()}", value: () -> V): ProducerRecord<String, V> {
        getOrCreateTopic<V>(topic).produce(key, value())
        return ProducerRecord(topic, 0, 0L, key, value())
    }

    override fun <V : Any> createConsumer(topic: TopicConfig, clazz: Class<V>): Consumer<String, V> {
        return ConsumerMock(this, getOrCreateTopic(topic.name))
    }

    override fun <V : Any> createProducer(topic: TopicConfig): Producer<String, V> {
        return ProducerMock(this, getOrCreateTopic(topic.name))
    }

    private fun <V> getOrCreateTopic(name: String): TestTopic<String, V> {
        return topics[name] as? TestTopic<String, V> ?: TestTopic<String, V>(name).also {
            topics[name] = it
            log.info("Created test topic $name")
        }
    }

    fun <V> awaitLastConsumed(topic: String): KeyValue<String, V> = runBlocking {
        withTimeout(timeoutMs) {
            getOrCreateTopic<V>(topic).lastConsumed()
        }
    }

    fun <V> awaitFirstConsumed(topic: String): KeyValue<String, V> = runBlocking {
        withTimeout(timeoutMs) {
            getOrCreateTopic<V>(topic).firstConsumed()
        }
    }

    fun <V> awaitConsumed(
        topic: String, query: MutableList<CompletableDeferred<Pair<String, V>>>.() -> List<KeyValue<String, V>>
    ): List<KeyValue<String, V>> = runBlocking {
        withTimeout(timeoutMs) {
            val testTopic = getOrCreateTopic<V>(topic)
            val consumedRecords = testTopic.consumed()
            query(consumedRecords)
        }
    }

    fun <V> awaitConsumed(take: Int, topic: String): List<KeyValue<String, V>> = runBlocking {
        withTimeout(timeoutMs) {
            getOrCreateTopic<V>(topic).takeConsumed(take)
        }
    }

    fun <V> awaitAllConsumed(topic: String): List<KeyValue<String, V>> = runBlocking {
        withTimeout(timeoutMs) {
            getOrCreateTopic<V>(topic).allConsumed()
        }
    }

    /**
     * Iterate over every produced record for a topic.
     * Wait up to [timeoutMs] for the list to contain all the expected items
     */
    fun <V> awaitProduced(
        topic: String,
        query: Flow<KeyValue<String, V>>.() -> Flow<KeyValue<String, V>>,
    ): List<KeyValue<String, V>> = runBlocking {
        val producedRecords = mutableSetOf<KeyValue<String, V>>()

        withTimeout(timeoutMs) {
            val coldFlowWithProducedRecords = channelFlow {
                while (true) {
                    getOrCreateTopic<V>(topic).allProduced().filterNot(producedRecords::contains)
                        .onEach(producedRecords::add).forEach { send(it) }

                    // make room for coroutine interrupt (cancellation)
                    delay(1)
                }
            }
            query(coldFlowWithProducedRecords).toList()
        }
    }
}
