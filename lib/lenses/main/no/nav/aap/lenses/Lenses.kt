package no.nav.aap.lenses

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * Functional Programming Lenses
 *
 * Use lenses to focus on a particular field of an immutable object.
 * The object is called a [whole] while the field/property is called the [part].
 * The lens laws (the implemented operations) is a composable pair of pure getter/setter functions.
 *
 * Lenses will empower the components of your code to be resilient by not having to change the logic through your code
 * when an object/state is refactored/changed. Instead, you only have to change the lenses - even globally if desirable.
 *
 * Lenses allow you to abstract state shape behind getters and setters. Where you otherwise litter your code with
 * deep diving into shapes to change the state of a particular field, you could import a lens.
 */
interface Lens<W, P> {
    /**
     *  The getter takes the [whole] and returns the [part] of the object that the lens is focused on
     */
    fun get(whole: W): P

    /**
     * The setter takes a whole, and a value to set the part to and returns the new whole with the updated part
     */
    fun set(whole: W, part: P): W

    fun update(whole: W, update: P.() -> P): W = set(whole, get(whole).update())

    operator fun invoke(whole: W): P = get(whole)
    operator fun invoke(whole: W, part: P): W = set(whole, part)
    operator fun invoke(whole: W, update: P.() -> P): W = update(whole, update)

    operator fun <P1> plus(next: Lens<P, P1>): Lens<W, P1> = object : Lens<W, P1> {
        override fun get(whole: W): P1 = next.get(this@Lens.get(whole))
        override fun set(whole: W, part: P1): W = this@Lens.set(whole, next.set(this@Lens.get(whole), part))
    }
}

/**
 * Create a lense for a [part], example: Person::name.lens()
 */
inline fun <reified W : Any, P> KProperty1<W, P>.lens(): Lens<W, P> = object : Lens<W, P> {
    override fun get(whole: W): P = KPropertyLens.forKClass(W::class).getterFor(this@lens)(whole)
    override fun set(whole: W, part: P): W = KPropertyLens.forKClass(W::class).setterFor(this@lens)(whole, part)
}

/**
 * Focus the lens on a [part] by accumulation, example: Person::name.lens() + Name::first
 */
inline operator fun <reified W : Any, reified P : Any, P1> Lens<W, P>.plus(next: KProperty1<P, P1>): Lens<W, P1> =
    this + next.lens()

/**
 * Focus the lens by accumulating [part], example: Person::name + Name::first
 */
inline operator fun <reified W : Any, reified P : Any, P1> KProperty1<W, P>.plus(next: KProperty1<P, P1>): Lens<W, P1> =
    this.lens() + next.lens()

/**
 * Focus the lens by accumulating lens to a [part], example: Person::name + Name::first.lens()
 */
inline operator fun <reified W : Any, reified P : Any, P1> KProperty1<W, P>.plus(next: Lens<P, P1>): Lens<W, P1> =
    this.lens() + next

/**
 * Get a [part] of a [whole]
 *
 * Example:
 * ```
 * val firstName = firstNameLens.get(person)
 * ```
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified W : Any, P> KProperty1<W, P>.get(whole: W): P = this.lens().get(whole)

/**
 * Set a [part] of a [whole]
 *
 * Example:
 * ```
 * val updatedPerson = firstNameLens.set(person, "Robin")
 * ```
 */
inline fun <reified W : Any, P> KProperty1<W, P>.set(whole: W, part: P): W = this.lens().set(whole, part)

/**
 * Update a [part] of a [whole]
 *
 * Example:
 * ```
 * val updatedPerson = firstNameLens.update(person) { "Robin" }
 * ```
 */
inline fun <reified W : Any, P> KProperty1<W, P>.update(whole: W, noinline part: P.() -> P) =
    this.lens().update(whole, part)

/**
 * Get the [part] from a [whole] through [operator overloading](https://kotlinlang.org/docs/operator-overloading.html#invoke-operator)
 *
 * Example:
 *  ```
 * val firstName = firstNameLens(person)
 *  ```
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline operator fun <reified W : Any, P> KProperty1<W, P>.invoke(whole: W): P = this.lens().invoke(whole)

/**
 * Set the [part] of a [whole] through property [operator overloading](https://kotlinlang.org/docs/operator-overloading.html#invoke-operator)
 *
 * Example:
 * ```
 * val updatedPerson = firstNameLens(person, "Robin")
 * ```
 */
inline operator fun <reified W : Any, P> KProperty1<W, P>.invoke(whole: W, part: P): W =
    this.lens().invoke(whole, part)

/**
 * Update the [part] of a [whole] through [operator overloading](https://kotlinlang.org/docs/operator-overloading.html#invoke-operator)
 *
 * Example:
 * ```
 * val updatedPerson = firstNameLens(person) { "Robin" }
 * ```
 */
inline operator fun <reified W : Any, P> KProperty1<W, P>.invoke(whole: W, noinline part: P.() -> P): W =
    this.lens().invoke(whole, part)

/**
 * Add default [part] for nullable lens
 *
 * Example:
 * ```
 * val middleName = middleNameLens(person) orElse "unknown"
 * ```
 */
infix fun <W, P> Lens<W, P?>.orElse(default: P): Lens<W, P> = object : Lens<W, P> {
    override fun get(whole: W): P = this@orElse.get(whole) ?: default
    override fun set(whole: W, part: P): W = this@orElse.set(whole, part)
}

/**
 * Add default [part] for nullable [part]
 *
 * Example:
 * ```
 * val middleNameOrUnknownLens = Name::middle orElse "unknown"
 * val middleName = middleNameOrUnknownLens(name)
 * ```
 */
inline infix fun <reified T : Any, V> KProperty1<T, V?>.orElse(default: V): Lens<T, V> = this.lens() orElse default

/**
 * Map for lenses when property is a list
 * TODO: test me
 */
inline fun <reified T, V> Lens<T, List<V>>.map(t: T, transform: V.() -> V): T =
    set(t, get(t).map {
        it.transform()
    })

@PublishedApi
internal class KPropertyLens<W : Any>(
    private val constructor: KFunction<W>,
    private val members: List<KProperty1<*, *>>,
    private val updatableMembers: Set<KCallable<*>>
) {
    companion object {
        private val cache: ConcurrentMap<KClass<*>, KPropertyLens<*>> = ConcurrentHashMap()

        @Suppress("UNCHECKED_CAST")
        fun <W : Any> forKClass(kclass: KClass<W>): KPropertyLens<W> =
            cache.computeIfAbsent(kclass) { forKClassUncached(it) } as KPropertyLens<W>

        private fun <W : Any> forKClassUncached(kclass: KClass<W>): KPropertyLens<W> {
            val properties = kclass.members.filterIsInstance<KProperty1<*, *>>().associateBy(KProperty1<*, *>::name)
            val constructor = kclass.constructors.filterContainsAll(properties).maxByOrNull { it.parameters.size }!!
            val members = constructor.parameters.mapNotNull { properties[it.name] }
            return KPropertyLens(constructor, members, members.toHashSet())
        }

        private fun <W : Any> Collection<KFunction<W>>.filterContainsAll(properties: Map<String, KProperty1<*, *>>): Collection<KFunction<W>> =
            filter { constructors -> properties.keys.containsAll(constructors.parameters.mapNotNull { it.name }) }
    }

    fun <P> getterFor(kProperty1: KProperty1<W, P>): (W) -> P = { whole -> kProperty1.get(whole) }

    fun <P> setterFor(property: KProperty1<W, P>): (W, P) -> W =
        if (updatableMembers.contains(property)) { whole, part -> copy(whole, property, part) }
        else throw IllegalArgumentException("Property $property not used in constructor $constructor")

    private fun <P> copy(whole: W, property: KCallable<*>, part: P): W {
        val args = members.map {
            if (it == property) part
            else it.call(whole)
        }
        return constructor.call(*args.toTypedArray())
    }
}
