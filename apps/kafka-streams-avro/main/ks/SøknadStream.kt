package ks

import kstream.logConsumed
import kstream.logProduced
import no.tordly.avro.aap.Status
import no.tordly.avro.aap.Soknad
import no.tordly.avro.aap.Vedtak
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal data class SøknadVedtak(val søknad: Soknad, val vedtak: Vedtak?)

fun StreamsBuilder.createSøknadStream(vedtakTable: KTable<String, Vedtak>) {
    stream<String, Soknad>("soknad.avro", Consumed.`as`("soknad-topic"))
        .logConsumed("log-consumed-avro-soknad")
        .selectKey({ _, søknad -> søknad.personident }, Named.`as`("select-key_soknad.personident"))
        .leftJoin(vedtakTable, ::valueJoiner, Joined.`as`("soknad-left-join-vedtak"))
        .filter({ _, (_, vedtak) -> vedtak == null }, Named.`as`("ignore-existing-vedtak"))
        .mapValues(::opprettVedtak, Named.`as`("create-vedtak"))
        .logProduced("vedtak.avro", "log-produce-created-avro-vedtak")
        .to("vedtak.avro", Produced.`as`("produce-created-avro-vedtak"))
}

private fun valueJoiner(søknad: Soknad, vedtak: Vedtak?): SøknadVedtak = SøknadVedtak(søknad, vedtak)
private fun opprettVedtak(søknadVedtak: SøknadVedtak): Vedtak =
    Vedtak(søknadVedtak.søknad.personident, null, Status.AVVENTER)

private val log: Logger = LoggerFactory.getLogger("app")
