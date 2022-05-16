/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge.graph

import ddd.fredgeorge.graph.Path.Companion.filter

// Understands its neighbors
class Node {
    private val links = mutableListOf<Link>()
    private val noVisitedNodes = emptyList<Node>()

    infix fun canReach(destination: Node) = paths(destination).isNotEmpty()
    infix fun hopCount(destination: Node) = path(destination, Path::hopCount).hopCount()
    infix fun cost(destination: Node) = path(destination).cost()
    infix fun path(destination: Node) = path(destination, Path::cost)
    infix fun paths(destination: Node) = paths().filter(destination)
    infix fun cost(amount: Number): LinkBuilder = LinkBuilder(amount.toDouble(), links)

    class LinkBuilder internal constructor(private val cost: Double, private val links: MutableList<Link>) {
        infix fun to(neighbor: Node): Node = neighbor.also { links.add(Link(cost, neighbor)) }
    }

    fun paths() = this.paths(noVisitedNodes)

    internal fun paths(visitedNodes: List<Node>): List<Path> {
        if (this in visitedNodes) return emptyList()
        return links.flatMap { link -> link.paths(visitedNodes + this) } + Path(this)
    }

    private fun path(destination: Node, strategy: PathStrategy) = paths(destination)
        .minByOrNull { strategy(it).toDouble() }
        ?: throw IllegalArgumentException("Destination cannot be reached")
}
