package vilkår

import io.ktor.server.testing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun <R> withTestApp(
    timeoutMs: Long = 1_000,
    test: TestApplicationEngine.(mocks: Mocks) -> R
): R = Mocks(timeoutMs).use { mocks ->
    withTestApplication(
        moduleFunction = { vilkår(kafka = mocks.kafka) },
        test = { test(mocks) }
    )
}

/**
 * The application will shut down instantly while the coroutines finishes their round.
 * To prevent the app to shut down before we can read the database,
 * we will force it to await gracefully ([timeoutMs]) to be able to read from the database before It's too late.
 * Note that [timeoutMs] should be smaller or equal to withTestApp(timeoutMs)
 */
fun <T> awaitDatabase(timeoutMs: Long = 1_000, query: suspend () -> T?): T? = runBlocking {
    withTimeoutOrNull(timeoutMs) {
        val coldFlow = channelFlow {
            while (true) newSuspendedTransaction(Dispatchers.IO) {
                query()?.let {
                    send(it)
                }
            }
        }
        coldFlow.firstOrNull()
    }
}
