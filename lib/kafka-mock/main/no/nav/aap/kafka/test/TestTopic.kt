package no.nav.aap.kafka.test

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.util.concurrent.atomic.AtomicInteger

typealias KeyValue<K, V> = Pair<K, V>

fun <V> KeyValue<String, V>.key() = first
fun <V> KeyValue<String, V>.value() = second

class TestTopic<K, V>(val name: String) {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private val offset = AtomicInteger(0)
    private val consumed = mutableListOf<CompletableDeferred<KeyValue<String, V>>>()
    private val produced = mutableListOf<KeyValue<String, V>>()

    fun produce(key: String, value: V) {
        val nextOffset = offset.getAndIncrement()
        val keyValue = KeyValue(key, value)
        produced.add(nextOffset, keyValue)
        consumed.add(nextOffset, CompletableDeferred(keyValue))

    }

    fun consume(offset: Int): List<KeyValue<String, V>> {
        return runBlocking {
            produced.sliceFrom(offset).onEachIndexed { index, record ->
                consumed[index].complete(record)
            }
        }
    }

    fun allProduced(): MutableList<Pair<String, V>> = produced

    fun consumed(): MutableList<CompletableDeferred<Pair<String, V>>> = consumed

    suspend fun takeConsumed(n: Int): List<KeyValue<String, V>> = consumed.take(n).awaitAll()
    suspend fun firstConsumed(): KeyValue<String, V> = consumed.first().await()
    suspend fun lastConsumed(): KeyValue<String, V> = consumed.last().await()
    suspend fun allConsumed(): List<KeyValue<String, V>> = consumed.awaitAll()
}

infix fun <T> List<T>.sliceFrom(startIndex: Int): List<T> = slice(startIndex until size)
