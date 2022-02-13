package vilkår.grunnlag

import no.nav.aap.kafka.models.KGrunnlag
import org.slf4j.LoggerFactory
import vilkår.*
import vilkår.vedtak.Vedtaksvurdering
import java.lang.invoke.MethodHandles

object ArbeidsevneStrategy : GrunnlagStrategy {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    override suspend fun applicable(value: KGrunnlag): Boolean = value.arbeidsevne != null

    override suspend fun execute(value: KGrunnlag) {
        log.trace("Utfører arbeidsevnevurdering")

        Database.getSøker(value.personident)!!
            .registrerArbeidsevne(value.arbeidsevne!!)
            .vurderArbeidsevne()
            .also { Database.update(it) }
            .let { Vedtaksvurdering.vurder(it) }
    }

    private fun Avklaringspenger.registrerArbeidsevne(prosent: Int) =
        arbeidsevneLens(this) { prosent }

    private fun Avklaringspenger.vurderArbeidsevne() = arbeidsevneVurderingLens(this) {
        when (opplysninger.arbeidsevne!! >= 50) {
            true -> Vurdering.OPPFYLT
            false -> Vurdering.AVSLÅTT
        }
    }
}
