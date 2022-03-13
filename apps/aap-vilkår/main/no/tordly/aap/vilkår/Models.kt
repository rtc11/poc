package no.tordly.aap.vilkår

import kotlinx.serialization.Serializable
import no.tordly.kotlinx.serde.LocalDateSerde
import no.tordly.kotlinx.serde.UUIDSerde
import no.tordly.lenses.lens
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

private val opplysningLens = Avklaringspenger::opplysninger.lens()
private val vurderingerLens = Avklaringspenger::vilkårsvurderinger.lens()
val statusLens = Avklaringspenger::status.lens()
val fødselsdatoLens = opplysningLens + Opplysninger::fødselsdato.lens()
val arbeidsevneLens = opplysningLens + Opplysninger::arbeidsevne.lens()
val alderVurderingLens = vurderingerLens + Vilkårsvurderinger::alderVilkår.lens() + Vilkår::vurdering.lens()
val arbeidsevneVurderingLens = vurderingerLens + Vilkårsvurderinger::arbeidsevneVilkår.lens() + Vilkår::vurdering.lens()

@Serializable
data class Avklaringspenger(
    val personident: String,

    @Serializable(UUIDSerde::class)
    val id: UUID = UUID.randomUUID(),
    val vilkårsvurderinger: Vilkårsvurderinger = Vilkårsvurderinger(),
    val opplysninger: Opplysninger = Opplysninger(),
    val status: Status = Status.AVVENTER,
) {
    fun isNotEndState() = status !in listOf(Status.VEDTATT, Status.AVSLÅTT)
}

@Serializable
data class Vilkårsvurderinger(
    val alderVilkår: Vilkår = Vilkår(Paragraf.`11-4`),
    val arbeidsevneVilkår: Vilkår = Vilkår(Paragraf.`11-5`),
) {
    fun isEndState(): Boolean = alderVilkår.isEndState() && arbeidsevneVilkår.isEndState()
}

@Serializable
data class Vilkår(
    val paragraf: Paragraf,
    val vurdering: Vurdering = Vurdering.VURDERES,
) {
    fun isEndState(): Boolean = vurdering in listOf(Vurdering.OPPFYLT, Vurdering.AVSLÅTT)
}

@Serializable
data class Opplysninger(
    @Serializable(LocalDateSerde::class)
    val fødselsdato: LocalDate? = null,
    val arbeidsevne: Int? = null,
) {
    val alder: Long? get() = fødselsdato?.until(LocalDate.now(), ChronoUnit.YEARS)
}

enum class Status { VEDTATT, AVSLÅTT, AVVENTER }
enum class Paragraf { `11-4`, `11-5` }
enum class Vurdering { VURDERES, OPPFYLT, AVSLÅTT }
