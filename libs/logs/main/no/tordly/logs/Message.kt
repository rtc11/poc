package no.tordly.logs

internal class Message internal constructor(private val logger: String, private val text: Any) {

    internal companion object {
        internal val LEVEL_AND_MESSAGE_STDOUT = { msg: Message, lvl: Level ->
            "${lvl.javaClass.simpleName.uppercase()} ${msg.logger} ${msg.text}"
        }
    }
}
