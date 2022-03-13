package vilkår.grunnlag

import kotlinx.coroutines.runBlocking
import no.nav.aap.kafka.models.KGrunnlag
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.junit.jupiter.api.Test
import vilkår.awaitDatabase
import vilkår.Avklaringspenger
import vilkår.Vurdering
import vilkår.alderVurderingLens
import vilkår.Database
import vilkår.Søkere
import vilkår.withTestApp
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GrunnlagAlderTest {
    // fixme: disabled teste by ocmmenting out test folder in build.gradle.kts
    @Test
    fun `et grunnlag med fødselsdato på grenseverdi 18 vurderes til oppfylt`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val nySøker = Avklaringspenger(ident)
            assertEquals(alderVurderingLens(nySøker), Vurdering.VURDERES)
            runBlocking { Database.save(nySøker) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForAlder(ident, LocalDate.now().minusYears(18))
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.fødselsdato neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.alderVilkår.vurdering, Vurdering.OPPFYLT)
            assertEquals(søker.opplysninger.alder, 18)
        }
    }

    @Test
    fun `et grunnlag med fødselsdato på grenseverdi 67 vurderes til oppfylt`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val nySøker = Avklaringspenger(ident)
            assertEquals(alderVurderingLens(nySøker), Vurdering.VURDERES)
            runBlocking { Database.save(nySøker) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForAlder(ident, LocalDate.now().minusYears(67))
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.fødselsdato neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.alderVilkår.vurdering, Vurdering.OPPFYLT)
            assertEquals(søker.opplysninger.alder, 67)
        }
    }

    @Test
    fun `et grunnlag med fødselsdato under 18 vurderes til avslått`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val nySøker = Avklaringspenger(ident)
            assertEquals(alderVurderingLens(nySøker), Vurdering.VURDERES)
            runBlocking { Database.save(nySøker) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForAlder(ident, LocalDate.now().minusYears(17))
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.fødselsdato neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.alderVilkår.vurdering, Vurdering.AVSLÅTT)
            assertEquals(søker.opplysninger.alder, 17)
        }
    }

    @Test
    fun `et grunnlag med fødselsdato over 67 vurderes til avslått`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            val nySøker = Avklaringspenger(ident)
            assertEquals(alderVurderingLens(nySøker), Vurdering.VURDERES)
            runBlocking { Database.save(nySøker) }

            mocks.kafka.produce("aap.grunnlag", ident) {
                KGrunnlag.createForAlder(ident, LocalDate.now().minusYears(68))
            }

            val søker = awaitDatabase {
                val condition = Op.build { Søkere.fødselsdato neq null and (Søkere.ident eq ident) }
                Database.getSøker(ident, condition)
            }

            assertNotNull(søker)
            assertEquals(søker.vilkårsvurderinger.alderVilkår.vurdering, Vurdering.AVSLÅTT)
            assertEquals(søker.opplysninger.alder, 68)
        }
    }

    private val randomIdent: String
        get() = Random.nextLong(10_000_000_000 until 99_999_999_999).toString()
}
