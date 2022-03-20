package no.tordly.logs

internal class Logger private constructor(
    private val name: String,
    private val level: Level,
    private val format: LogFormat
) {
    private constructor(name: String) : this(name, Level.Trace)
    private constructor(name: String, format: LogFormat) : this(name, Level.Trace, format)
    private constructor(name: String, level: Level) : this(name, level, Message.STDOUT)

    fun trace(output: Any) = log(output, Level.Trace)
    fun debug(output: Any) = log(output, Level.Debug)
    fun info(output: Any) = log(output, Level.Info)
    fun warn(output: Any) = log(output, Level.Warn)
    fun error(output: Any) = log(output, Level.Error)

    private fun log(output: Any, outputLevel: Level) = level.log(Message(name, output), outputLevel, format)

    companion object {
        inline fun <reified T : Any> forFormat(noinline format: LogFormat) = Logger(T::class.simpleName!!, format)
        inline fun <reified T : Any> default() = Logger(T::class.simpleName!!)
        inline fun <reified T : Any> forLevel(level: Level) = Logger(T::class.simpleName!!, level)
    }
}

internal inline fun <reified T : Any> T.debug(msg: String) = also { lazy { Logger.default<T>() }.value.debug(msg) }
internal inline fun <reified T : Any> T.info(msg: String) = also { lazy { Logger.default<T>() }.value.info(msg) }
internal inline fun <reified T : Any> T.warn(msg: String) = also { lazy { Logger.default<T>() }.value.warn(msg) }
