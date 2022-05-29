package vilkår.vedtak

import kotlinx.coroutines.runBlocking
import no.tordly.aap.vilkår.*
import no.tordly.aap.vilkår.vedtak.Vedtaksvurdering
import no.tordly.ktor.essentials.loadConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VedtakTest {

    @Test
    fun `alle oppfylte vilkår vil gi vedtak`() {
        val config = loadConfig<Config>()
        Database.connect(config.db)
        val vedtaksvurdering = Vedtaksvurdering

        val aap = Avklaringspenger("123", status = Status.AVVENTER)

        runBlocking {
            Database.save(aap)
        }

        val aapMedEvne = arbeidsevneVurderingLens(aap) { Vurdering.OPPFYLT }
        val aapMedEvneOgAlder = alderVurderingLens(aapMedEvne) { Vurdering.OPPFYLT }


        val actual = runBlocking {
            vedtaksvurdering.vurder(aapMedEvneOgAlder)
            Database.getSøker("123")
        }

        assertNotNull(actual)
        assertEquals(actual.status, Status.VEDTATT)
    }
}
