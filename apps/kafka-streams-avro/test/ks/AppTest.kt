package ks

import no.tordly.avro.aap.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class AppTest {

    @Test
    fun `happy path`() {
        KStreamMock(createTopology()).use { kafka ->
            val søknadTopic = kafka.createInputTopic<Soknad>("soknad.avro")
            val medlemTopic = kafka.createInputTopic<Medlem>("medlem.avro")
            val vedtakTopic = kafka.createOutputTopic<Vedtak>("vedtak.avro")

            assertTrue(vedtakTopic.isEmpty) // intially empty

            val søknad = Soknad("123", false)
            søknadTopic.pipeInput("123", søknad)

            val createdVedtak = vedtakTopic.readValue()
            assertNull(createdVedtak.erMedlem)

            assertTrue(vedtakTopic.isEmpty) // no update yet

            val medlem = Medlem("123", UUID.randomUUID().toString(), null, Response(ErMedlem.JA, null))
            medlemTopic.pipeInput("123", medlem)

            val updatedVedtak = vedtakTopic.readKeyValue()
            assertEquals("123", updatedVedtak.key)
            assertEquals(ErMedlem.JA, updatedVedtak.value.erMedlem)
            assertEquals(Status.VEDTATT, updatedVedtak.value.status)

            assertTrue(vedtakTopic.isEmpty) // verify that every produced record is asserted
        }
    }
}
