package ks

import no.tordly.json.Medlem
import no.tordly.json.Status
import no.tordly.json.Søknad
import no.tordly.json.Vedtak
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AppTest {

    @Test
    fun `happy path`() {
        KStreamMock(createTopology()).use { kafka ->
            val søknadTopic = kafka.createJsonInputTopic<Søknad>("soknad.json")
            val medlemTopic = kafka.createJsonInputTopic<Medlem>("medlem.json")
            val vedtakTopic = kafka.createJsonOutputTopic<Vedtak>("vedtak.json")

            assertTrue(vedtakTopic.isEmpty) // intially empty

            val søknad = Søknad("123", false)
            søknadTopic.pipeInput("123", søknad)

            val createdVedtak = vedtakTopic.readValue()
            assertNull(createdVedtak.erMedlem)

            assertTrue(vedtakTopic.isEmpty) // no update yet

            val medlem = Medlem("123", response = Medlem.Response(Medlem.Response.Svar.JA, null))
            medlemTopic.pipeInput("123", medlem)

            val updatedVedtak = vedtakTopic.readKeyValue()
            assertEquals("123", updatedVedtak.key)
            assertEquals("JA", updatedVedtak.value.erMedlem)
            assertEquals(Status.VEDTATT, updatedVedtak.value.status)

            assertTrue(vedtakTopic.isEmpty) // verify that every produced record is asserted
        }
    }
}
