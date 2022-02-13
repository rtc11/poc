package vilkår

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import ktor.essentials.loadConfig
import no.nav.aap.kafka.Kafka
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.KafkaFactory
import no.nav.aap.kafka.TopicConfig
import vilkår.grunnlag.GrunnlagFlow
import vilkår.søknad.SøknadFlow
import vilkår.vedtak.VedtakStatsistiJob

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
