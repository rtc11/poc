/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge.quantity

import kotlin.math.roundToLong

class Unit {

    companion object {
        internal const val EPSILON = 1e-10

        private val TEASPOON = Unit()
        private val TABLESPOON = Unit(3, TEASPOON)
        private val OUNCE = Unit(2, TABLESPOON)
        private val CUP = Unit(8, OUNCE)
        private val PINT = Unit(2, CUP)
        private val QUART = Unit(2, PINT)
        private val GALLON = Unit(4, QUART)

        val Number.teaspoons get() = RatioQuantity(this, TEASPOON)
        val Number.tablespoons get() = RatioQuantity(this, TABLESPOON)
        val Number.ounces get() = RatioQuantity(this, OUNCE)
        val Number.cups get() = RatioQuantity(this, CUP)
        val Number.pints get() = RatioQuantity(this, PINT)
        val Number.quarts get() = RatioQuantity(this, QUART)
        val Number.gallons get() = RatioQuantity(this, GALLON)

        private val INCH = Unit()
        private val FOOT = Unit(12, INCH)
        private val YARD = Unit(3, FOOT)
        private val FATHOM = Unit(6, FOOT)
        private val CHAIN = Unit(22, YARD)
        private val FURLONG = Unit(10, CHAIN)
        private val MILE = Unit(8, FURLONG)
        private val LEAGUE = Unit(3, MILE)

        val Number.inches get() = RatioQuantity(this, INCH)
        val Number.feet get() = RatioQuantity(this, FOOT)
        val Number.yards get() = RatioQuantity(this, YARD)
        val Number.fathoms get() = RatioQuantity(this, FATHOM)
        val Number.chains get() = RatioQuantity(this, CHAIN)
        val Number.furlongs get() = RatioQuantity(this, FURLONG)
        val Number.miles get() = RatioQuantity(this, MILE)
        val Number.leagues get() = RatioQuantity(this, LEAGUE)

        private val CELSIUS = Unit()
        private val FAHRENHEIT = Unit(5/9.0, 32, CELSIUS)
        private val GAS_MARK = Unit(125/9.0, -218.0/25, CELSIUS)
        private val KELVIN = Unit(1, 273.15, CELSIUS)
        private val RANKINE = Unit(5/9.0, 491.67, CELSIUS)

        val Number.celsius get() = IntervalQuantity(this, CELSIUS)
        val Number.fahrenheit get() = IntervalQuantity(this, FAHRENHEIT)
        val Number.gasMark get() = IntervalQuantity(this, GAS_MARK)
        val Number.kelvin get() = IntervalQuantity(this, KELVIN)
        val Number.rankine get() = IntervalQuantity(this, RANKINE)
    }

    private val baseUnit: Unit
    private val baseUnitRatio: Double
    private val offset: Double

    private constructor() {
        baseUnit = this
        baseUnitRatio = 1.0
        offset = 0.0
    }

    private constructor(relativeRatio: Number, relativeUnit: Unit) :
            this(relativeRatio, 0.0, relativeUnit)

    private constructor(relativeRatio: Number, offset: Number, relativeUnit: Unit) {
        baseUnit = relativeUnit.baseUnit
        baseUnitRatio = relativeRatio.toDouble() * relativeUnit.baseUnitRatio
        this.offset = offset.toDouble()
    }

    internal fun convertedAmount(otherAmount: Double, other: Unit) =
        ((otherAmount - other.offset) * other.baseUnitRatio / this.baseUnitRatio + this.offset).also {
            require(this.isCompatible(other)) { "Units are not compatible" }
        }

    internal fun hashCode(amount: Double) =
        ((amount - offset) * baseUnitRatio / EPSILON).roundToLong().hashCode()

    internal fun isCompatible(other: Unit) = this.baseUnit == other.baseUnit
}