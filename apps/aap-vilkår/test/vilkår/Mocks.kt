package vilkår

import no.tordly.kafka.test.KafkaMock
import org.jetbrains.exposed.sql.transactions.transaction

class Mocks(timeoutMs: Long) : AutoCloseable {
    val kafka = KafkaMock(timeoutMs)

    override fun close() {
        //language=text
        transaction {
            exec("SET REFERENTIAL_INTEGRITY FALSE")
            exec("TRUNCATE TABLE søkere")
            exec("TRUNCATE TABLE vilkårer")
            exec("SET REFERENTIAL_INTEGRITY TRUE")
        }
    }
}
