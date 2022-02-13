package vilkår.vedtak

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import vilkår.Database
import vilkår.Status
import java.io.Closeable
import java.lang.invoke.MethodHandles

class VedtakStatsistiJob(context: CoroutineDispatcher) : Closeable {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private val job = CoroutineScope(context).launch {
        while (true) {
            delay(1_000L)
            logStats()
        }
    }

    private suspend fun logStats() {
        val groupedByStatus = Database.getAllSøkere()
            .groupBy { it.status }
            .mapValues { (_, value) -> value.size }

        val vedtatt = "VEDTATT:${groupedByStatus[Status.VEDTATT]}"
        val avslått = "AVSLÅTT:${groupedByStatus[Status.AVSLÅTT]}"
        val avventer = "AVVENTER:${groupedByStatus[Status.AVVENTER]}"
        val total = "TOTAL:${groupedByStatus.map { it.value }.sumOf { it }}"

        log.info("$vedtatt $avslått $avventer $total")
    }

    override fun close() {
        if (!job.isCompleted) runBlocking { job.cancelAndJoin() }
    }
}
