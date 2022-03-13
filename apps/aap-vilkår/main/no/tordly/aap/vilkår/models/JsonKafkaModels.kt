package no.tordly.aap.vilkår.models

import no.tordly.lenses.lens
import java.time.LocalDate
import java.util.*

val søknadIdLens = KSøknad::id.lens()

data class KSøknad(
    val personident: String,
    val id: UUID? = null,
)

data class KGrunnlag(
    val personident: String,
    val fødselsdato: LocalDate? = null,
    val arbeidsevne: Int? = null,
) {
    companion object {
        fun createForAlder(ident: String, fødselsdato: LocalDate) = KGrunnlag(ident, fødselsdato = fødselsdato)
        fun createForArbeidsevne(ident: String, prosent: Int) = KGrunnlag(ident, arbeidsevne = prosent)
    }
}
