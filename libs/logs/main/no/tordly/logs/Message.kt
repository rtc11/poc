package no.tordly.logs

import com.google.gson.Gson

internal class Message internal constructor(private val logger: String, private val text: Any) {

    internal companion object {
        private val gson = Gson()

        val STDOUT = { msg: Message, lvl: Level -> "${lvl.javaClass.simpleName.uppercase()} ${msg.logger} ${msg.text}" }

        val JSON = { msg: Message, lvl: Level ->
            gson.toJson(
                mapOf(
                    "level" to lvl.javaClass.simpleName.uppercase(),
                    "logger" to msg.logger,
                    "message" to msg.text
                )
            )
        }
    }
}
