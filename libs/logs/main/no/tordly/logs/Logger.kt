package no.tordly.logs

internal class Logger private constructor(private val name: String, private val level: Level) {
    fun trace(output: Any) = log(output, Level.Trace)
    fun debug(output: Any) = log(output, Level.Debug)
    fun info(output: Any) = log(output, Level.Info)
    fun warn(output: Any) = log(output, Level.Warn)
    fun error(output: Any) = log(output, Level.Error)

    private fun log(output: Any, outputLevel: Level) {
        val msg = Message(name, output)
        level.log(msg, outputLevel, Message.LEVEL_AND_MESSAGE_STDOUT)
    }

    companion object {
        inline fun <reified T : Any> forLevel(level: Level) = Logger(T::class.simpleName!!, level)
        inline fun <reified T : Any> forName(name: String) = Logger(name, Level.Info)
        inline fun <reified T : Any> default() = Logger(T::class.simpleName!!, Level.Trace)
    }
}

internal inline fun <reified T : Any> T.debug(msg: String) = also { lazy { Logger.default<T>() }.value.debug(msg) }
internal inline fun <reified T : Any> T.info(msg: String) = also { lazy { Logger.default<T>() }.value.info(msg) }
internal inline fun <reified T : Any> T.warn(msg: String) = also { lazy { Logger.default<T>() }.value.warn(msg) }
