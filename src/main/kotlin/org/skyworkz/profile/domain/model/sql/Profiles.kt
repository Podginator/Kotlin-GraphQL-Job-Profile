package org.skyworkz.profile.domain.model.sql

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.model.sql.ProfileTag.primaryKey


object Profiles : IntIdTable() {
    val firstName = varchar("first_name", 36)
    val lastName = varchar("last_name", 36)
    val email = varchar("email", 60).uniqueIndex()
    val password = varchar("password", 256)
    val available = bool("available").default(false)
    val minimumHourlyRate = float("minimum_hourly_rate").default(0.0f)
    val cvLink = text("cv_link").nullable()
    val approved = bool("approved").default(false)
}

object ProfileTag : Table() {
    val profile = reference("Profiles", Profiles).primaryKey(0)
    val tag = reference("Tags", Tags).primaryKey(1)
}

object ProfileSubscriptions : Table() {
    val profile = ProfileSubscriptions.reference("subscriber", Profiles).primaryKey(0)
    val subscription = ProfileSubscriptions.reference("subscribee", Profiles).primaryKey(1)
}

fun ResultRow.toUser() : User {
    return User(
        id = this[Profiles.id].value,
        firstName = this[Profiles.firstName],
        lastName  = this[Profiles.lastName],
        email = this[Profiles.email],
        password = this[Profiles.password],
        approved = this[Profiles.approved],
        available = this[Profiles.available],
        minimumHourlyRate = this[Profiles.minimumHourlyRate]
    )
}