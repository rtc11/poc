package ks

import no.nav.aap.json.Medlem
import no.nav.aap.json.Status
import no.nav.aap.json.Søknad
import no.nav.aap.json.Vedtak
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AppTest {

    @Test
    fun `happy path`() {
        val kafkaStreams = KStreamMock(topology = createTopology())
        val søknadTopic = kafkaStreams.createJsonInputTopic<Søknad>("soknad.json")
        val medlemTopic = kafkaStreams.createJsonInputTopic<Medlem>("medlem.json")
        val vedtakTopic = kafkaStreams.createJsonOutputTopic<Vedtak>("vedtak.json")

        assertTrue(vedtakTopic.isEmpty) // intially empty

        val søknad = Søknad("123", false)
        søknadTopic.pipeInput("123", søknad)

        val createdVedtak = vedtakTopic.readKeyValue()
        assertEquals("123", createdVedtak.key)
        assertNull(createdVedtak.value.erMedlem)

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
