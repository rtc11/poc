package vilkår.grunnlag

import kotlinx.coroutines.runBlocking
import no.nav.aap.kafka.models.KGrunnlag
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.junit.jupiter.api.Test
import vilkår.awaitDatabase
import vilkår.Avklaringspenger
import vilkår.Vurdering
import vilkår.arbeidsevneVurderingLens
import vilkår.Database
import vilkår.Søkere
import vilkår.withTestApp
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GrunnlagArbeidsevneTest {

    @Test
    fun `et grunnlag med arbeidsevne over 50 oppdaterer vurderes til oppfylt`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val aap = Avklaringspenger(ident)
            assertEquals(arbeidsevneVurderingLens(aap), Vurdering.VURDERES)
            runBlocking { Database.save(aap) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForArbeidsevne(ident, 51)
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.arbeidsevne neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.arbeidsevneVilkår.vurdering, Vurdering.OPPFYLT)
            assertEquals(søker.opplysninger.arbeidsevne, 51)
        }
    }

    @Test
    fun `et grunnlag med arbeidsevne på grenseverdi 50 vurderes til oppfylt`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val aap = Avklaringspenger(ident)
            assertEquals(arbeidsevneVurderingLens(aap), Vurdering.VURDERES)
            runBlocking { Database.save(aap) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForArbeidsevne(ident, 50)
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.arbeidsevne neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.arbeidsevneVilkår.vurdering, Vurdering.OPPFYLT)
            assertEquals(søker.opplysninger.arbeidsevne, 50)
        }
    }

    @Test
    fun `et grunnlag med arbeidsevne under 50 vurderes til avslått`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val aap = Avklaringspenger(ident)
            assertEquals(arbeidsevneVurderingLens(aap), Vurdering.VURDERES)
            runBlocking { Database.save(aap) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForArbeidsevne(ident, 49)
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.arbeidsevne neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.arbeidsevneVilkår.vurdering, Vurdering.AVSLÅTT)
            assertEquals(søker.opplysninger.arbeidsevne, 49)
        }
    }

    private val randomIdent: String
        get() = Random.nextLong(10_000_000_000 until 99_999_999_999).toString()
}
