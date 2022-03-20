package no.tordly.logs

import no.tordly.logs.Log.Companion.debugThreshold
import no.tordly.logs.Log.Companion.infoThreshold
import org.junit.Test

internal class LogsTest {

    @Test
    fun `default logger`() {
        Log.debug("hello debug")
        Log.info("hello info")
        debug("hello debug")
        info("hello info")
    }

    @Test
    fun `debug logger`() {
        val log = debugThreshold<LogsTest>()
        log.debug("hello debug")
        log.info("hello info")
    }

    @Test
    fun `info logger`() {
        val log = infoThreshold<LogsTest>()
        log.debug("hello debug")
        log.info("hello info")
    }
}
