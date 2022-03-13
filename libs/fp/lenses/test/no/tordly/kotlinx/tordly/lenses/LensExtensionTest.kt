package no.tordly.kotlinx.tordly.lenses

import no.tordly.lenses.Lens
import no.tordly.lenses.lens
import no.tordly.lenses.plus
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("USELESS_IS_CHECK")
internal class LensExtensionTest {

    @Test
    fun `lens from property`() {
        val streetLens = Employee::person.lens()
        assertTrue(streetLens is Lens<Employee, Person>)
    }

    @Test
    fun `lens = property + property`() {
        val streetLens = Employee::person + Person::name
        assertTrue { streetLens is Lens<Employee, Name> }
    }

    @Test
    fun `lens = property + lens`() {
        val streetLens = Employee::person + Person::name.lens()
        assertTrue { streetLens is Lens<Employee, Name> }
    }

    @Test
    fun `lens = lens + lens`() {
        val streetLens = Employee::person.lens() + Person::name.lens()
        assertTrue { streetLens is Lens<Employee, Name> }
    }

    @Test
    fun `lens = lens + property`() {
        val streetLens = Employee::person.lens() + Person::name
        assertTrue { streetLens is Lens<Employee, Name> }
    }

    private data class Employee(val person: Person)
    private data class Person(val name: Name)
    private data class Name(val first: String, val last: String, val middle: String? = null)
}
