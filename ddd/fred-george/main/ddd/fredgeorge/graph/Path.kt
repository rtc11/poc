package ddd.fredgeorge.graph

import ddd.fredgeorge.graph.Link.Companion.cost

// Understands a route from one Node to another
class Path internal constructor(private val destination: Node) {
    private val links = mutableListOf<Link>()

    internal infix fun prepend(link: Link) = links.add(0, link)

    fun cost() = links.cost()
    fun hopCount() = links.size

    companion object {
        internal fun List<Path>.filter(destination: Node) = this.filter { it.destination == destination }
    }
}

internal typealias PathStrategy = (Path) -> Number
