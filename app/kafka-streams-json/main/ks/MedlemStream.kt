package ks

import kstream.json.consumedWithJson
import kstream.json.joinedWithJson
import kstream.json.producedWithJson
import kstream.logConsumed
import kstream.logProduced
import no.nav.aap.json.Medlem
import no.nav.aap.json.Status
import no.nav.aap.json.Vedtak
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Named
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun StreamsBuilder.createMedlemStream(vedtakTable: KTable<String, Vedtak>) {
    stream<String, Medlem>("medlem.json", consumedWithJson("consume-json-medlem"))
        .logConsumed("consumed-json-medlem-logged")
        .selectKey({ _, medlem -> medlem.personident }, Named.`as`("select-key_medlem.personident"))
        .join(vedtakTable, ::medlemJoiner, joinedWithJson("medlem-join-vedtak"))
        .mapValues(::oppdaterVedtak, Named.`as`("add-medlem"))
        .logProduced("vedtak.json", "produce-json-vedtak-with-medlem-logged")
        .to("vedtak.json", producedWithJson("produce-json-vedtak-with-medlem"))
}

internal data class MedlemVedtak(
    val medlem: Medlem,
    val vedtak: Vedtak
)

private fun medlemJoiner(medlem: Medlem, vedtak: Vedtak): MedlemVedtak =
    MedlemVedtak(medlem, vedtak)

private fun oppdaterVedtak(medlemVedtak: MedlemVedtak): Vedtak {
    val (medlem, vedtak) = medlemVedtak

    val status = when (medlem.response!!.erMedlem) {
        Medlem.Response.Svar.JA -> Status.VEDTATT
        Medlem.Response.Svar.NEI -> Status.AVSLÅTT
        Medlem.Response.Svar.UAVKLART -> Status.AVVENTER
    }

    return vedtak.copy(
        erMedlem = medlem.response!!.erMedlem.name,
        status = status,
    )
}

private val log: Logger = LoggerFactory.getLogger("app")
