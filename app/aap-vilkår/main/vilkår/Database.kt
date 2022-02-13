package vilkår

import kotlinx.coroutines.Dispatchers
import no.nav.aap.lenses.plus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String,
)

object Database {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    fun connect(config: DatabaseConfig) {
        connect(url = config.url, user = config.username, password = config.password)

        transaction {
            SchemaUtils.create(Søkere, Vilkårer)
        }
    }

    suspend fun save(aap: Avklaringspenger) = newSuspendedTransaction(Dispatchers.IO) {
        Søkere.insert(toDto(aap))
        Vilkårer.insert(toDto(aap.vilkårsvurderinger.alderVilkår, aap.personident))
        Vilkårer.insert(toDto(aap.vilkårsvurderinger.arbeidsevneVilkår, aap.personident))

        log.trace("Saved $aap")
    }

    suspend fun update(aap: Avklaringspenger): Unit = newSuspendedTransaction(Dispatchers.IO) {
        val identCondition = Op.build { Vilkårer.søker eq aap.personident }
        val alderCondition = Op.build { Vilkårer.paragraf eq aap.vilkårsvurderinger.alderVilkår.paragraf }
        val arbeidsevneCondition = Op.build { Vilkårer.paragraf eq aap.vilkårsvurderinger.arbeidsevneVilkår.paragraf }

        Vilkårer.update({ identCondition and alderCondition }) {
            it[vurdering] = aap.vilkårsvurderinger.alderVilkår.vurdering
        }

        Vilkårer.update({ identCondition and arbeidsevneCondition }) {
            it[vurdering] = aap.vilkårsvurderinger.arbeidsevneVilkår.vurdering
        }

        Søkere.update({ Søkere.ident eq aap.personident }) {
            it[status] = aap.status
            it[fødselsdato] = aap.opplysninger.fødselsdato
            it[arbeidsevne] = aap.opplysninger.arbeidsevne
        }

        log.trace("Updated $aap")
    }

    suspend fun hasSøker(personident: String): Boolean =
        newSuspendedTransaction(Dispatchers.IO) {
            Søkere.select(Søkere.ident eq personident).any()
        }

    suspend fun getSøker(personident: String): Avklaringspenger? =
        newSuspendedTransaction(Dispatchers.IO) {
            Søkere.select(Søkere.ident eq personident)
                .map(::toAvklaringspenger)
                .map(::innerJoinVilkår)
                .singleOrNull()
        }

    suspend fun getSøker(personident: String, whereCondition: Op<Boolean>): Avklaringspenger? =
        newSuspendedTransaction(Dispatchers.IO) {
            Søkere.select(Søkere.ident eq personident and whereCondition)
                .map(::toAvklaringspenger)
                .map(::innerJoinVilkår)
                .singleOrNull()
        }

    suspend fun getAllSøkere(): List<Avklaringspenger> =
        newSuspendedTransaction(Dispatchers.IO) {
            Søkere.selectAll()
                .map(::toAvklaringspenger)
                .map(::innerJoinVilkår)
        }
}

fun innerJoinVilkår(aap: Avklaringspenger): Avklaringspenger {
    val alderVilkårLens = Avklaringspenger::vilkårsvurderinger + Vilkårsvurderinger::alderVilkår
    fun Avklaringspenger.withAlderVilkår(): Avklaringspenger = alderVilkårLens(this) {
        Vilkårer.select((Vilkårer.søker eq aap.personident) and (Vilkårer.paragraf eq aap.vilkårsvurderinger.alderVilkår.paragraf))
            .map(::toVilkår)
            .single()
    }

    val arbeidsevneVilkårLens = Avklaringspenger::vilkårsvurderinger + Vilkårsvurderinger::arbeidsevneVilkår
    fun Avklaringspenger.withArbeidsevneVilkår(): Avklaringspenger = arbeidsevneVilkårLens(this) {
        Vilkårer.select((Vilkårer.søker eq aap.personident) and (Vilkårer.paragraf eq aap.vilkårsvurderinger.arbeidsevneVilkår.paragraf))
            .map(::toVilkår)
            .single()
    }

    return aap.withAlderVilkår().withArbeidsevneVilkår()
}

fun toAvklaringspenger(row: ResultRow): Avklaringspenger =
    Avklaringspenger(
        personident = row[Søkere.ident],
        status = row[Søkere.status],
        opplysninger = Opplysninger(
            fødselsdato = row[Søkere.fødselsdato],
            arbeidsevne = row[Søkere.arbeidsevne]
        )
    )

private fun toVilkår(row: ResultRow) = Vilkår(
    paragraf = row[Vilkårer.paragraf],
    vurdering = row[Vilkårer.vurdering]
)

private fun toDto(vilkår: Vilkår, ident: String): Vilkårer.(InsertStatement<Number>) -> Unit = { vilkårer ->
    vilkårer[søker] = ident
    vilkårer[paragraf] = vilkår.paragraf
    vilkårer[vurdering] = vilkår.vurdering
}

private fun toDto(aap: Avklaringspenger): Søkere.(InsertStatement<Number>) -> Unit = { søker ->
    søker[ident] = aap.personident
    søker[status] = aap.status
    søker[fødselsdato] = aap.opplysninger.fødselsdato
    søker[arbeidsevne] = aap.opplysninger.arbeidsevne
}

object Søkere : Table() {
    val ident = varchar("ident", 11)
    val status = enumerationByName("status", 8, Status::class).default(Status.AVVENTER)
    val fødselsdato = date("fødselsdato").nullable()
    val arbeidsevne = integer("arbeidsevne").nullable()

    override val primaryKey = PrimaryKey(ident)
}

object Vilkårer : IntIdTable() {
    val paragraf = enumerationByName("paragraf", 6, Paragraf::class)
    val vurdering = enumerationByName("vurdering", 8, Vurdering::class)
    val søker = varchar("søkere_ident", 11) references Søkere.ident
}
