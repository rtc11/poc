package no.tordly.lenses

import no.tordly.lenses.orElse
import no.tordly.lenses.plus
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LensLawsTest {
    // Lens
    private val firstNameLens = Employee::person + Person::name + Name::first
    private val middleNameLens = Employee::person + Person::name + Name::middle

    // Whole
    private val employee = Employee(Person(Name("Ola", "Normann")))

    @Test
    fun `get part from whole`() {
        val actual = firstNameLens.get(employee)
        assertEquals("Ola", actual)
    }

    @Test
    fun `get part from whole through operator overloading`() {
        val actual = firstNameLens(employee)
        assertEquals("Ola", actual)
    }

    @Test
    fun `set part on whole`() {
        val expected = Employee(Person(Name("Kari", "Normann")))
        val actual = firstNameLens.set(employee, "Kari")
        assertEquals(expected, actual)
    }

    @Test
    fun `set part on whole through operator overloading`() {
        val expected = Employee(Person(Name("Kari", "Normann")))
        val actual = firstNameLens(employee, "Kari")
        assertEquals(expected, actual)
    }

    @Test
    fun `update part on whole`() {
        val expected = Employee(Person(Name("Kari", "Normann")))
        val actual = firstNameLens.update(employee) { "Kari" }
        assertEquals(expected, actual)
    }

    @Test
    fun `update part on whole through operator overloading`() {
        val expected = Employee(Person(Name("Kari", "Normann")))
        val actual = firstNameLens(employee) { "Kari" }
        assertEquals(expected, actual)
    }

    @Test
    fun `nullable lens`() {
        val middleName = middleNameLens(employee)
        assertEquals(null, middleName)
    }

    @Test
    fun `orElse on lense with default value`() {
        val middleNameOrUnknownLense = middleNameLens orElse "unknown"
        val middleName = middleNameOrUnknownLense(employee)
        assertEquals("unknown", middleName)
    }

    @Test
    fun `orElse on property with default value`() {
        val middleNameOrUnknownLense = Name::middle orElse "unknown"
        val middleName = middleNameOrUnknownLense(employee.person.name)
        assertEquals("unknown", middleName)
    }

    internal data class Employee(val person: Person)
    internal data class Person(val name: Name)
    internal data class Name(val first: String, val last: String, val middle: String? = null)
}
