import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotNull

internal class SøkerTest {

    @Test
    fun test() {
        val vedtak = Vedtak()
        val visitor = TestVisitor()
        vedtak.accept(visitor)

        assertNotNull(visitor.id)
    }
}

internal class TestVisitor : VedtakVisitor {
    lateinit var id: UUID

    override fun visit(id: UUID) {
        this.id = id
    }
}
