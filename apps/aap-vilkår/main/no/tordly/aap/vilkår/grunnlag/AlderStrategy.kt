package no.tordly.aap.vilkår.grunnlag

import no.tordly.aap.vilkår.*
import no.tordly.aap.vilkår.models.KGrunnlag
import org.slf4j.LoggerFactory
import no.tordly.aap.vilkår.vedtak.Vedtaksvurdering
import java.lang.invoke.MethodHandles
import java.time.LocalDate

object AlderStrategy : GrunnlagStrategy {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    override suspend fun applicable(value: KGrunnlag): Boolean = value.fødselsdato != null

    override suspend fun execute(value: KGrunnlag) {
        log.trace("Utfører aldersvurdering")

        Database.getSøker(value.personident)!!
            .registrerAlder(value.fødselsdato!!)
            .vurderAlder()
            .also { Database.update(it) }
            .let { Vedtaksvurdering.vurder(it) }
    }

    private fun Avklaringspenger.registrerAlder(dato: LocalDate) =
        fødselsdatoLens(this) { dato }

    private fun Avklaringspenger.vurderAlder() = alderVurderingLens(this) {
        when (opplysninger.alder!! in 18..67) {
            true -> Vurdering.OPPFYLT
            false -> Vurdering.AVSLÅTT
        }
    }
}
