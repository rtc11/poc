package vilkår.vedtak

import vilkår.Avklaringspenger
import vilkår.Status
import vilkår.Vurdering
import vilkår.statusLens
import vilkår.Database

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

