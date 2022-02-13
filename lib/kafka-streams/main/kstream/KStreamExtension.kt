package kstream

import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.ValueTransformerWithKey
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier
import org.apache.kafka.streams.processor.ProcessorContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("app")

fun <K, V> KStream<K, V>.logProduced(topic: String, name: String): KStream<K, V> =
    peek({ key, value -> log.info("Produced [$topic] K:$key V:$value") }, Named.`as`(name))

fun <K, V : Any> KStream<K, V>.logConsumed(name: String): KStream<K, V> = transformValues(
    ValueTransformerWithKeySupplier {
        object : ValueTransformerWithKey<K, V, V> {
            private lateinit var context: ProcessorContext

            override fun init(context: ProcessorContext) {
                this.context = context
            }

            override fun transform(readOnlyKey: K, value: V): V {
                log.info("Consumed [${context.topic()}] K:$readOnlyKey V:$value")
                return value
            }

            override fun close() {}
        }
    }, Named.`as`(name)
)
