/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge

import ddd.fredgeorge.order.bestOrNull
import ddd.fredgeorge.probability.Chance
import ddd.fredgeorge.quantity.RatioQuantity
import ddd.fredgeorge.quantity.Unit.Companion.celsius
import ddd.fredgeorge.quantity.Unit.Companion.cups
import ddd.fredgeorge.quantity.Unit.Companion.fahrenheit
import ddd.fredgeorge.quantity.Unit.Companion.gallons
import ddd.fredgeorge.quantity.Unit.Companion.ounces
import ddd.fredgeorge.quantity.Unit.Companion.quarts
import ddd.fredgeorge.rectangle.Rectangle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class OrderableTest {

    @Test
    fun `rectangle with largest area`() {
        assertEquals(36.0, listOf(Rectangle(2, 3), Rectangle.square(6.0), Rectangle(5, 7.0)).bestOrNull()?.area())
        assertNull(emptyList<Rectangle>().bestOrNull())
    }

    @Test
    internal fun `least likely chance`() {
        assertEquals(Chance(0), listOf(Chance(0.1), Chance(0.7), Chance(0), Chance(0.9)).bestOrNull())
        assertNull(emptyList<Chance>().bestOrNull())
    }

    @Test
    fun `largest Quantity`() {
        assertEquals(2.quarts, listOf(0.2.gallons, 24.ounces, 0.5.gallons, 7.cups).bestOrNull())
        assertNull(emptyList<RatioQuantity>().bestOrNull())
        assertEquals(100.celsius, listOf(212.fahrenheit, 0.celsius, 50.fahrenheit, (-40).celsius).bestOrNull())
    }
}
