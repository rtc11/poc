internal interface Visitable<R> {
    fun accept(visitor: R)
}
