package vilkår.søknad

import kotlinx.coroutines.flow.take
import no.nav.aap.kafka.models.KSøknad
import no.nav.aap.kafka.test.key
import no.nav.aap.kafka.test.value
import org.junit.jupiter.api.Test
import vilkår.awaitDatabase
import vilkår.Database
import vilkår.withTestApp
import java.util.*
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class SøknadTest {

    @Test
    fun `vi lagrer søknader som ikke har en id`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            mocks.kafka.produce("aap.soknad", ident) {
                KSøknad(ident)
            }

            val søker = awaitDatabase {
                Database.getSøker(ident)
            }

            assertNotNull(søker)
        }
    }

    @Test
    fun `vi ignorerer søknader som har id`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            mocks.kafka.produce("aap.soknad", ident) {
                KSøknad(ident, UUID.randomUUID())
            }

            val søker = awaitDatabase(300) {
                Database.getSøker(ident)
            }

            assertNull(søker)
        }
    }

    @Test
    fun `lagrede søknader legges tilbake på topic med id`() {
        withTestApp { mocks ->
            val ident by lazy { randomIdent }

            mocks.kafka.produce("aap.soknad", ident) {
                KSøknad(ident)
            }

            val producedSøknader = mocks.kafka.awaitProduced<KSøknad>("aap.soknad") {
                take(2)
            }

            assertEquals(producedSøknader.size, 2)
            assertEquals(producedSøknader.last().key(), ident)
            assertNotNull(producedSøknader.last().value().id)
        }
    }

    private val randomIdent: String
        get() = Random.nextLong(10_000_000_000 until 99_999_999_999).toString()
}
