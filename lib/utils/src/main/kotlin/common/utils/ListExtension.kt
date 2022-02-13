package common.utils

/**
 * Replace item in list with where item from list matches a predicate
 */
fun <T> List<T>.replace(newValue: T, predicate: (T) -> Boolean): List<T> {
    return map {
        if (predicate(it)) newValue else it
    }
}
