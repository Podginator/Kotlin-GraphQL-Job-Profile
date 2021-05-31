package org.skyworkz.profile.domain.model.sql

import com.google.gson.Gson
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.skyworkz.profile.domain.model.Tag
import org.skyworkz.profile.domain.model.request.CVDto
import org.skyworkz.profile.domain.model.sql.ProfileTag.primaryKey

object CV : IntIdTable() {
    val content = text("content").nullable()
    val profileId = CV.reference("Profiles", Profiles)
}

fun ResultRow.toCV() : CVDto? {
    return this[CV.content]?.let{ Gson().fromJson(it, CVDto::class.java) }
}