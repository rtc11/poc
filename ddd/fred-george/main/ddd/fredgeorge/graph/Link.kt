package ddd.fredgeorge.graph

// Understands a connection from one Node to another
internal class Link internal constructor(private val cost: Double, private val target: Node) {
    internal fun paths(visitedNodes: List<Node>) = target.paths(visitedNodes).onEach { it.prepend(this) }

    companion object {
        internal fun List<Link>.cost() = this.sumOf { it.cost }
    }
}
