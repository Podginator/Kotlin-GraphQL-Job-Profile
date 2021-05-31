package org.skyworkz.profile.domain.repository

class PagedResult<T>(
    val totalSize: Int,
    val data : List<T>
) {
    val size = data.size
}
