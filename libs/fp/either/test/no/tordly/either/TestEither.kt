package no.tordly.either

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestEither {

    @Test
    fun `either returns right value`() {
        val number = Random.nextInt(11, 20)
        val either: Either<String, Int> = if (number <= 10) Either.left("under ten") else Either.right(number)

        assertTrue(either is Right)
        assertEquals(number, either.value)
    }

    @Test
    fun `either returns left value`() {
        val number = Random.nextInt(11)
        val either: Either<String, Int> = if (number <= 10) Either.left("under ten") else Either.right(number)

        assertTrue(either is Left)
        assertEquals("under ten", either.value)
    }

    @Test
    fun `either folds`() {
        val number = Random.nextInt(6)
        val either: Either<String, Int> = if (number <= 10) Either.left("under ten") else Either.right(number)
        val answer = either.either({ s -> "It was lower: $s" }, { i -> "It was higher: $i" })

        assertEquals("It was lower: under ten", answer)
    }
}
