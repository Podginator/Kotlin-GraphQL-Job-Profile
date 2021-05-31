package org.skyworkz.profile.domain.model.sql

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.skyworkz.profile.domain.model.Tag

object Tags : IntIdTable() {
    val name = varchar("name", 34)
}

fun ResultRow.toTag() : Tag {
    return Tag(
        id = this[Tags.id].value,
        name = this[Tags.name]
    )
}