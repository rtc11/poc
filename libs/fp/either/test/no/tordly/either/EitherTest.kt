package no.tordly.either

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class EitherTest {

    @Test
    fun `right side is 7`() {
        val seven = IntOrMod10String.create(7)
        assertTrue(seven is Either.Right)
        assertEquals(7, seven.value.num)
    }

    @Test
    fun `left side is 'mod 10'`() {
        val mod10 = IntOrMod10String.create(10)
        assertTrue(mod10 is Either.Left)
        assertEquals("mod 10", mod10.value)
    }

    @Test
    fun `right side 29 becomse string 'mod 10' when adding +1 with flatMap`() {
        val number = IntOrMod10String.create(29)
        assertTrue(number is Either.Right)
        assertEquals(29, number.value.num)

        val mod10 = number.flatMap { it.add(1) }
        assertTrue(mod10 is Either.Left)
        assertEquals("mod 10", mod10.value)
    }

    @Test
    fun `subtract with flatmap`() {
        val test = IntOrMod10String.create(29)
            .flatMap { it.subtract(2) }

        println(test)
        assertTrue(test is Either.Right)
        assertEquals(27, test.value.num)
    }
}

// This class is locked as mod 10 if it becomes left
private class IntOrMod10String private constructor(val num: Int) {
    fun add(amount: Int): Either<String, IntOrMod10String> = create(num + amount)
    fun subtract(amount: Int): Either<String, IntOrMod10String> = create(num - amount)

    override fun toString(): String = "$num"

    companion object {
        fun create(num: Int): Either<String, IntOrMod10String> =
            when (num % 10 == 0) {
                true -> Either.Left("mod 10")
                false -> Either.Right(IntOrMod10String(num))
            }
    }
}