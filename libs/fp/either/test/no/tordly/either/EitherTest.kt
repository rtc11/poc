package no.tordly.either

import org.junit.Assert.assertEquals
import org.junit.Test

internal class EitherTest {

    @Test
    fun `map should apply a fn when either is right`() {
        assertEquals(Either.Right(2), Either.Right(1).map { it + 1 })
    }

    @Test
    fun `map should not apply a fn when either is left`() {
        val either: Either<String, Int> = Either.Left("Some Error")
        assertEquals(Either.Left("Some Error"), either.map { it + 1 })
    }

    @Test
    fun `flatmap should apply a fn when either is right`() {
        val justInc = { n: Int -> Either.Right(n + 1) }
        assertEquals(Either.Right(2), Either.Right(1).flatMap(justInc))
    }

    @Test
    fun `flatmap should not apply a fn when either is left`() {
        val justInc = { n: Int -> Either.Right(n + 1) }
        val either: Either<String, Int> = Either.Left("Some Error")
        assertEquals(Either.Left("Some Error"), either.flatMap(justInc))
    }
}
