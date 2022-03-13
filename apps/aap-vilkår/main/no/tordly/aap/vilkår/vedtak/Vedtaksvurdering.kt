package no.tordly.aap.vilkår.vedtak

import no.tordly.aap.vilkår.Avklaringspenger
import no.tordly.aap.vilkår.Status
import no.tordly.aap.vilkår.Vurdering
import no.tordly.aap.vilkår.statusLens
import no.tordly.aap.vilkår.Database

object Vedtaksvurdering {
    suspend fun vurder(søker: Avklaringspenger) {
        if (søker.isNotEndState() && søker.vilkårsvurderinger.isEndState()) {
            val evneOppfylt = søker.vilkårsvurderinger.arbeidsevneVilkår.vurdering == Vurdering.OPPFYLT
            val alderOppfylt = søker.vilkårsvurderinger.alderVilkår.vurdering == Vurdering.OPPFYLT

            val vedtak = when (evneOppfylt && alderOppfylt) {
                true -> statusLens(søker) { Status.VEDTATT }
                else -> statusLens(søker) { Status.AVSLÅTT }
            }

            Database.update(vedtak)
        }
    }
}

