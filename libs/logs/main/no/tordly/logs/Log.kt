package no.tordly.logs

class Log private constructor(private val name: String, private val level: LogLevel) {
    fun info(msg: String) = level.log(msg, LogLevel.INFO)
    fun debug(msg: String) = level.log(msg, LogLevel.DEBUG)

    companion object {
        internal inline fun <reified T : Any> infoThreshold() = Log(T::class.simpleName!!, LogLevel.INFO)
        internal inline fun <reified T : Any> debugThreshold() = Log(T::class.simpleName!!, LogLevel.DEBUG)
    }
}

internal interface Out {
    fun log(msg: String, level: LogLevel)
}

internal typealias LogStrategy = (LogLevel) -> String

internal sealed class LogLevel(private val order: Int, private val name: String) : Out {
    override fun log(msg: String, other: LogLevel) {
        if (other.order >= this.order) println("[${other.name}] $msg")
    }

    internal object TRACE : LogLevel(0, "TRACE")
    internal object DEBUG : LogLevel(1, "DEBUG")
    internal object INFO : LogLevel(2, "INFO")
    internal object WARN : LogLevel(3, "WARN")
    internal object ERROR : LogLevel(4, "ERROR")
}

internal inline fun <reified T : Any> T.info(msg: String) = Log.infoThreshold<T>().info(msg)
internal inline fun <reified T : Any> T.debug(msg: String) = Log.debugThreshold<T>().info(msg)
