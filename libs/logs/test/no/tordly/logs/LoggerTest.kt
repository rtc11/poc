package no.tordly.logs

import org.junit.jupiter.api.Test

internal class LoggerTest {

    @Test
    fun `default logger`() {
        debug("hello debug")
        info("hello info")
        warn("hello warn")
    }

    @Test
    fun `debug logger`() {
        val log = Logger.forLevel<LoggerTest>(Level.Debug)
        log.trace("hello trace")
        log.debug("hello debug")
        log.info("hello info")
        log.warn("hello warn")
    }

    @Test
    fun `info logger`() {
        val log = Logger.default<LoggerTest>()
        log.trace("hello trace")
        log.debug("hello debug")
        log.info("hello info")
        log.warn("hello warn")
    }

    @Test
    fun `json logger`() {
        val log = Logger.forFormat<LoggerTest>(Message.JSON)
        log.trace("hello trace")
        log.debug("hello debug")
        log.info("hello info")
        log.warn("hello warn")
    }
}
