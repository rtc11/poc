package ks

import kstream.logConsumed
import kstream.logProduced
import no.nav.aap.avro.ErMedlem
import no.nav.aap.avro.Medlem
import no.nav.aap.avro.Status
import no.nav.aap.avro.Vedtak
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Named
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal data class MedlemVedtak(val medlem: Medlem, val vedtak: Vedtak)

fun StreamsBuilder.createMedlemStream(vedtakTable: KTable<String, Vedtak>) {
    stream<String, Medlem>("medlem.avro", Consumed.`as`("medlem-topic"))
        .logConsumed("log-consumed-avro-medlem")
        .selectKey({ _, medlem -> medlem.personident }, Named.`as`("select-key_medlem.personident"))
        .join(vedtakTable, ::medlemJoiner)
        .mapValues(::oppdaterVedtak, Named.`as`("add-medlem"))
        .logProduced("vedtak.avro", "log-produce-avro-vedtak-with-medlem")
        .to("vedtak.avro")
}

private fun medlemJoiner(medlem: Medlem, vedtak: Vedtak): MedlemVedtak =
    MedlemVedtak(medlem, vedtak)

private fun oppdaterVedtak(medlemVedtak: MedlemVedtak): Vedtak {
    val (medlem, vedtak) = medlemVedtak.medlem to medlemVedtak.vedtak

    val status = when (medlem.response.erMedlem) {
        ErMedlem.JA -> Status.VEDTATT
        ErMedlem.NEI -> Status.AVSLÅTT
        ErMedlem.UAVKLART -> Status.AVVENTER
        null -> error("missing svar")
    }

    return vedtak.apply {
        this.erMedlem = medlem.response.erMedlem
        this.status = status
    }
}

private val log: Logger = LoggerFactory.getLogger("app")
