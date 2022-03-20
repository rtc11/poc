package no.tordly.logs

internal typealias LogFormat = (Message, Level) -> Any

internal val LogFormat.STDOUT get() = Message.STDOUT
internal val LogFormat.JSON get() = Message.JSON

internal sealed class Level(private val order: Int) {
    internal object Trace : Level(0)
    internal object Debug : Level(1)
    internal object Info : Level(2)
    internal object Warn : Level(3)
    internal object Error : Level(4)

    internal fun log(msg: Message, level: Level, format: LogFormat) {
        if (level.order >= order) println(format(msg, level))
    }
}
