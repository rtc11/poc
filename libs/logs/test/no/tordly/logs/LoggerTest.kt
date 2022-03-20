package no.tordly.logs

import org.junit.Test

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
        log.debug("hello debug")
        log.info("hello info")
        log.warn("hello warn")
    }

    @Test
    fun `info logger`() {
        val log = Logger.default<LoggerTest>()
        log.debug("hello debug")
        log.info("hello info")
        log.warn("hello warn")
    }

    @Test
    fun `named logger`() {
        val log = Logger.forName<LoggerTest>("Something Different")
        log.debug("hello debug")
        log.info("hello info")
        log.warn("hello warn")
    }
}
