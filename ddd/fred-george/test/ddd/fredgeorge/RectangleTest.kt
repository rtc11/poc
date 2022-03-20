/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge

import ddd.fredgeorge.rectangle.Rectangle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Ensures Rectangle operates correctly
internal class RectangleTest {

    @Test
    fun area() {
        assertEquals(24.0, Rectangle(4.0, 6.0).area())
        assertEquals(24.0, Rectangle(4, 6).area())
        assertEquals(24.0, Rectangle(4, 6).area)
        assertEquals(25.0, Rectangle.square(5).area())
    }

    @Test
    fun perimeter() {
        assertEquals(20.0, Rectangle(4.0, 6.0).perimeter())
        assertEquals(20.0, Rectangle(4, 6).perimeter())
        assertEquals(20.0, Rectangle(4, 6).perimeter)
        assertEquals(20.0, Rectangle.square(5.0).perimeter())
    }

    @Test
    fun `range checks`() {
        assertThrows<IllegalArgumentException> { Rectangle(0, 6.0) }
        assertThrows<IllegalArgumentException> { Rectangle(4.0, 0) }
        assertThrows<IllegalArgumentException> { Rectangle.square(0) }
    }
}