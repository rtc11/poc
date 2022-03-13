package no.tordly.kafka.extensions

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Future
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Send and log for traceing
 */
fun <K, V> Producer<K, V>.sendAndLog(
    record: ProducerRecord<K, V>,
    log: Logger = LoggerFactory.getLogger("kafka"),
): Future<RecordMetadata> =
    send(record) { metadata, exception ->
        if (exception != null) log.warn("Failed to produce record: ${exception.localizedMessage}")
        else log.trace("Produced[${metadata.topic()}]=${record.value()}")
    }

/**
 * A non-blocking producer send
 */
suspend fun <K, V> Producer<K, V>.sendAsync(
    record: ProducerRecord<K, V>,
    log: Logger = LoggerFactory.getLogger("kafka"),
): RecordMetadata =
    suspendCoroutine { continuation ->
        send(record) { metadata, exception ->
            if (metadata == null) continuation.resumeWithException(exception!!).also {
                log.warn("Failed to produce record: ${exception.localizedMessage}")
            }
            else continuation.resume(metadata).also {
                log.trace("Produced[${metadata.topic()}]=${record.value()}")
            }
        }
    }
