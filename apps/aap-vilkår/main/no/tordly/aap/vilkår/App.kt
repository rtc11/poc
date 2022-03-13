package no.tordly.aap.vilkår

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import no.tordly.ktor.essentials.loadConfig
import no.tordly.kafka.Kafka
import no.tordly.kafka.KafkaConfig
import no.tordly.kafka.KafkaFactory
import no.tordly.kafka.TopicConfig
import no.tordly.aap.vilkår.grunnlag.GrunnlagFlow
import no.tordly.aap.vilkår.søknad.SøknadFlow
import no.tordly.aap.vilkår.vedtak.VedtakStatsistiJob

data class Config(
    val kafka: KafkaConfig,
    val søknad: TopicConfig,
    val grunnlag: TopicConfig,
    val db: DatabaseConfig,
)

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::vilkår).start(wait = true)
}

fun Application.vilkår(
    config: Config = loadConfig(),
    kafka: Kafka = KafkaFactory(config.kafka),
) {
    install(ContentNegotiation) { json(Json { prettyPrint = true }) }

    Database.connect(config.db)
    val context = Dispatchers.Default

    val søknadFlow = SøknadFlow(kafka, config, context)
    val grunnlagFlow = GrunnlagFlow(kafka, config, context)
    val statisticJob = VedtakStatsistiJob(context)

    environment.monitor.subscribe(ApplicationStopping) {
        søknadFlow.close()
        grunnlagFlow.close()
        statisticJob.close()
    }

    routing {
        restAPI()
    }
}

private fun Routing.restAPI() {
    route("/api/søker") {
        get {
            call.respond(Database.getAllSøkere())
        }
    }
}
