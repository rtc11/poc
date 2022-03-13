package no.tordly.either

interface F<T, R> {
    fun f(t: T): R
}

sealed interface Either<A, B> {
    fun <X> either(left: (t: A) -> X, right: (t: B) -> X): X
    fun swap(): Either<B, A>

    companion object {
        fun <A, B> left(a: A): Left<A, B> = Left(a)
        fun <A, B> right(b: B): Right<A, B> = Right(b)
    }
}

class Left<A, B>(val value: A) : Either<A, B> {
    override fun <X> either(left: (t: A) -> X, right: (t: B) -> X): X = left(value)
    override fun swap(): Either<B, A> = Either.right(value)
    override fun toString(): String = value.toString()
}

class Right<A, B>(val value: B) : Either<A, B> {
    override fun <X> either(left: (t: A) -> X, right: (t: B) -> X): X = right(value)
    override fun swap(): Either<B, A> = Either.left(value)
    override fun toString(): String = value.toString()
}
