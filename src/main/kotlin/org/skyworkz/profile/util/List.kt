package org.skyworkz.profile.util


fun <T, V> List<T>.sortByListOrder(originalOrder: List<V>, accessorFunction: (T) -> V) : List<T?> {
    val mapVersion = this.associateBy({ accessorFunction(it)}, { it })
    return originalOrder.map { mapVersion[it] }
}