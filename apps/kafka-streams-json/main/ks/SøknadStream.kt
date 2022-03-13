package ks

import kstream.logConsumed
import kstream.logProduced
import kstream.json.consumedWithJson
import kstream.json.joinedWithJson
import kstream.json.producedWithJson
import no.tordly.json.Status
import no.tordly.json.Søknad
import no.tordly.json.Vedtak
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Named
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal data class SøknadVedtak(val søknad: Søknad, val vedtak: Vedtak?)

fun StreamsBuilder.createSøknadStream(vedtakTable: KTable<String, Vedtak>) {
    stream<String, Søknad>("soknad.json", consumedWithJson("consume-json-soknad"))
        .logConsumed("log-consumed-json-soknad")
        .selectKey({ _, søknad -> søknad.personident }, Named.`as`("select-key_soknad.personident"))
        .leftJoin(vedtakTable, ::valueJoiner, joinedWithJson("soknad-leftjoin-vedtak"))
        .filter({ _, (_, vedtak) -> vedtak == null }, Named.`as`("ignore-existing-vedtak"))
        .mapValues(::opprettVedtak, Named.`as`("create-vedtak"))
        .logProduced("vedtak.json", "log-produce-created-json-vedtak")
        .to("vedtak.json", producedWithJson("produce-created-json-vedtak"))
}

private fun valueJoiner(søknad: Søknad, vedtak: Vedtak?): SøknadVedtak = SøknadVedtak(søknad, vedtak)
private fun opprettVedtak(søknadVedtak: SøknadVedtak): Vedtak =
    Vedtak(søknadVedtak.søknad.personident, null, Status.AVVENTER)

private val log: Logger = LoggerFactory.getLogger("app")
