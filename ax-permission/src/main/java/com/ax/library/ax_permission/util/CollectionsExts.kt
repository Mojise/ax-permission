package com.ax.library.ax_permission.util

/**
 * Returns index of the first element matching the given [predicate], or -1 if the list does not contain such element.
 */
internal inline fun <T> List<T>.indexOfFirst(
    startIndex: Int,
    predicate: (T) -> Boolean,
): Int {
    for (index in startIndex..lastIndex) {
        if (predicate(this[index])) {
            return index
        }
    }
    return -1
}