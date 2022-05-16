import java.util.*

internal class Søker {
    private val tidslinje = Tidslinje()
    private val historikk = Historikk()

    internal fun håndterVedtak(vedtak: Vedtak) = historikk.leggTilVedtak(vedtak)
}

internal class Tidslinje

internal class Vedtak : Visitable<VedtakVisitor> {
    private val id: UUID = UUID.randomUUID()

    override fun accept(visitor: VedtakVisitor) = visitor.visit(id)
}

internal class Historikk {
    private val vedtaks = mutableListOf<Vedtak>()

    internal fun leggTilVedtak(vedtak: Vedtak) = vedtaks.add(vedtak)
}

internal interface VedtakVisitor {
    fun visit(id: UUID) {}
}
