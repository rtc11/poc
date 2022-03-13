package no.tordly.kafka

import io.ktor.utils.io.charsets.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class KotlinxSerializer<T : Any> : Serializer<T> {
    companion object {
        val json = Json { prettyPrint = true }
    }

    override fun serialize(topic: String, data: T): ByteArray {
        return json.encodeToString(data::class.java).toByteArray(Charsets.UTF_8)
    }
}

@ExperimentalSerializationApi
class kotlinxDeserializer<T : Any>(private val clazz: Class<T>) : Deserializer<T> {
    companion object {
        val json = Json { prettyPrint = true }
    }

    override fun deserialize(topic: String, data: ByteArray): T {
        val t: Class<T> = json.decodeFromString(data.toString(Charsets.UTF_8))
        return t.cast(clazz)
    }
}
