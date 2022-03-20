/*
 * Copyright (c) 2022 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George  fredgeorge@acm.org
 */

package ddd.fredgeorge.order

interface Orderable<T> {
    infix fun isBetterThan(other: T): Boolean
}

fun <S: Orderable<S>> List<S>.bestOrNull(): S? {
    var champion: S? = null
    for (challenger in this) {
        if (champion == null || challenger isBetterThan champion) champion = challenger
    }
    return champion
}
