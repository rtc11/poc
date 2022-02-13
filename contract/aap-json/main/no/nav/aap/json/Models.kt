package no.nav.aap.json

import java.time.LocalDate
import java.util.*


data class Søknad(
    val personident: String,
    val arbeidetUtenlands: Boolean = false,
)

data class Vedtak(
    val personident: String,
    val erMedlem: String? = null,
    val status: Status = Status.AVVENTER,
)

enum class Status { VEDTATT, AVSLÅTT, AVVENTER }

data class Medlem(
    val personident: String,
    val id: UUID = UUID.randomUUID(),
    val request: Request? = null,
    val response: Response? = null,
) {
    data class Request(
        val mottattDato: LocalDate = LocalDate.now(),
        val ytelse: String = "AAP",
        val arbeidetUtenforNorgeSiste6År: Boolean = false,
    )

    data class Response(
        val erMedlem: Svar,
        val årsak: String? = null,
    ) {
        enum class Svar {
            JA,
            NEI,
            UAVKLART
        }
    }
}
