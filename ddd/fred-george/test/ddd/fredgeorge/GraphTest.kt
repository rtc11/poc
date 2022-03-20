/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge

import ddd.fredgeorge.graph.Node
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GraphTest {
    companion object {
        private val A = Node()
        private val B = Node()
        private val C = Node()
        private val D = Node()
        private val E = Node()
        private val F = Node()
        private val G = Node()

        init {
            B cost 5 to A
            B cost 6 to C cost 7 to D cost 2 to E cost 3 to B cost 4 to F
            C cost 1 to D
            C cost 8 to E
        }
    }

    @Test fun `can reach`() {
        assertTrue(B canReach B)
        assertTrue(B canReach A)
        assertTrue(B canReach F)
        assertTrue(B canReach D)
        assertTrue(C canReach F)
        assertFalse(G canReach B)
        assertFalse(A canReach B)
        assertFalse(B canReach G)
    }

    @Test fun `hop count`() {
        assertEquals(0, B hopCount B)
        assertEquals(1, B hopCount A)
        assertEquals(1, B hopCount F)
        assertEquals(2, B hopCount D)
        assertEquals(3, C hopCount F)
        assertThrows<IllegalArgumentException>{ G hopCount B }
        assertThrows<IllegalArgumentException>{ A hopCount B }
        assertThrows<IllegalArgumentException>{ B hopCount G }
    }

    @Test internal fun cost() {
        assertEquals(0.0, B cost B)
        assertEquals(5.0, B cost A)
        assertEquals(4.0, B cost F)
        assertEquals(7.0, B cost D)
        assertEquals(10.0, C cost F)
        assertThrows<IllegalArgumentException> { G cost B }
        assertThrows<IllegalArgumentException> { A cost B }
        assertThrows<IllegalArgumentException> { B cost G }
    }

    @Test fun path() {
        assertPath(A, A, 0, 0)
        assertPath(B, A, 1, 5)
        assertPath(B, F, 1, 4)
        assertPath(B, D, 2, 7)
        assertPath(C, F, 4, 10)
        assertThrows<IllegalArgumentException> { G path B }
        assertThrows<IllegalArgumentException> { A path B }
        assertThrows<IllegalArgumentException> { B path G }
    }

    @Test fun `all paths between two Nodes`() {
        assertEquals(1, (A paths A).size)
        assertEquals(1, (B paths A).size)
        assertEquals(1, (B paths F).size)
        assertEquals(2, (B paths D).size)
        assertEquals(3, (C paths F).size)
        assertEquals(0, (G paths B).size)
        assertEquals(0, (B paths G).size)
        assertEquals(0, (A paths B).size)
    }

    @Test fun `all paths from one Nodes`() {
        assertEquals(1, A.paths().count())
        assertEquals(9, B.paths().count())
        assertEquals(15, C.paths().count())
        assertEquals(6, D.paths().count())
        assertEquals(7, E.paths().count())
        assertEquals(1, F.paths().count())
        assertEquals(1, G.paths().count())
    }

    private fun assertPath(source: Node, destination: Node, expectedHopCount: Int, expectedCost: Number) {
        (source path destination).also { path ->
            assertEquals(expectedHopCount, path.hopCount())
            assertEquals(expectedCost.toDouble(), path.cost())
        }
    }
}