package vilkår.grunnlag

import no.nav.aap.kafka.models.KGrunnlag
import org.slf4j.LoggerFactory
import vilkår.*
import vilkår.vedtak.Vedtaksvurdering
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
