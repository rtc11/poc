/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge

import ddd.fredgeorge.probability.Chance
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ChanceTest {

    companion object {
        private val IMPOSSIBLE = Chance(0)
        private val UNLIKELY = Chance(0.25)
        private val EQUALLY_LIKELY = Chance(0.5)
        private val LIKELY = Chance(0.75)
        private val CERTAIN = Chance(1)
    }

    @Test
    fun equality() {
        assertEquals(Chance(0.75), LIKELY)
        assertNotEquals(LIKELY, UNLIKELY)
        assertNotEquals(LIKELY, Any())
        assertNotEquals(LIKELY, null)
    }

    @Test
    fun `Chance in sets`() {
        assertTrue(Chance(0.75) in hashSetOf(LIKELY))
        assertEquals(1, hashSetOf(LIKELY, Chance(0.75)).size)
    }

    @Test
    fun hash() {
        assertEquals(LIKELY.hashCode(), Chance(0.75).hashCode())
        assertEquals(Chance(0.3).hashCode(), (!!Chance(0.3)).hashCode())
    }

    @Test
    fun not() {
        assertEquals(UNLIKELY, LIKELY.not())
        assertEquals(LIKELY, LIKELY.not().not())
        assertEquals(LIKELY, !!LIKELY)
        assertEquals(IMPOSSIBLE, CERTAIN.not())
        assertEquals(EQUALLY_LIKELY, EQUALLY_LIKELY.not())
        assertEquals(Chance(0.3), !!Chance(0.3))
    }

    @Test
    fun and() {
        assertEquals(UNLIKELY, EQUALLY_LIKELY and EQUALLY_LIKELY)
        assertEquals(Chance(0.1875), LIKELY and UNLIKELY)
        assertEquals(LIKELY and UNLIKELY, UNLIKELY and LIKELY)
        assertEquals(IMPOSSIBLE, LIKELY and IMPOSSIBLE)
        assertEquals(LIKELY, CERTAIN and LIKELY)
    }

    @Test
    internal fun or() {
        assertEquals(LIKELY, EQUALLY_LIKELY or EQUALLY_LIKELY)
        assertEquals(Chance(0.8125), LIKELY or UNLIKELY)
        assertEquals(LIKELY or UNLIKELY, UNLIKELY or LIKELY)
        assertEquals(LIKELY, LIKELY or IMPOSSIBLE)
        assertEquals(CERTAIN, CERTAIN or LIKELY)
    }

    @Test
    internal fun `invalid fractions`() {
        assertThrows<IllegalArgumentException> { Chance(-0.01) }
        assertThrows<IllegalArgumentException> { Chance(1.01) }
    }
}
