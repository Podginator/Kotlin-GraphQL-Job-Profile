package org.skyworkz.profile.domain.model.sql

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.skyworkz.profile.domain.model.Job

object Jobs : IntIdTable() {
    val position = text("position").nullable()
    val description = text("description").nullable()
    val hourlyRate  = float("hourlyRate")
    val createdBy = reference("createdBy", Profiles)
    val createdAt = datetime("createdAt")
}


object JobTags : Table() {
    val job = reference("Jobs", Jobs,  onDelete = ReferenceOption.CASCADE).primaryKey(0)
    val tag = reference("Tag", Tags).primaryKey(1)
}
