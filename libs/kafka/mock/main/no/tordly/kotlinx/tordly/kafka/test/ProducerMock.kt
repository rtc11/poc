package no.tordly.kotlinx.tordly.kafka.test

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

internal class ProducerMock<V>(
    private val kafka: KafkaMock,
    private val topic: TestTopic<String, V>,
    private val log: Logger = LoggerFactory.getLogger("kafka"),
) : Producer<String, V> {

    override fun send(record: ProducerRecord<String, V>): Future<RecordMetadata> {
        kafka.produce(topic.name, record.key()) {
            record.value()
        }

        log.info("Produced[$topic]=${record.value()}")

        return CompletableFuture.completedFuture(metadata)
    }

    override fun send(record: ProducerRecord<String, V>, callback: Callback): Future<RecordMetadata> =
        send(record).also {
            callback.onCompletion(it.get(), null)
        }

    private val metadata: RecordMetadata
        get() {
            val partition = TopicPartition(topic.name, -1)
            return RecordMetadata(partition, 0L, 0L, 0L, 0L, 0, 0)
        }

    override fun close() {}
    override fun close(t: Duration) = TODO("dead end")
    override fun initTransactions() = TODO("dead end")
    override fun beginTransaction() = TODO("dead end")
    override fun commitTransaction() = TODO("dead end")
    override fun abortTransaction() = TODO("dead end")
    override fun flush() = TODO("dead end")
    override fun partitionsFor(t: String) = TODO("dead end")
    override fun metrics() = TODO("dead end")
    override fun sendOffsetsToTransaction(o: Offsets, g: String) = TODO("dead end")
    override fun sendOffsetsToTransaction(o: Offsets, m: ConsumerGroupMetadata) = TODO("dead end")
}

internal typealias Offsets = MutableMap<TopicPartition, OffsetAndMetadata>
