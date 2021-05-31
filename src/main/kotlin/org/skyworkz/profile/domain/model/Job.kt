package org.skyworkz.profile.domain.model

import org.jetbrains.exposed.sql.ResultRow
import org.joda.time.DateTime
import org.skyworkz.profile.domain.model.sql.Jobs
import org.skyworkz.profile.domain.model.sql.toUser

data class Job(
    val id : Int?,
    val position: String?,
    val description: String?,
    val hourlyRate: Float,
    val tags: MutableList<Tag> = mutableListOf(),
    val createdBy: User? = null,
    val createdAt: DateTime = DateTime.now()
) {
    companion object {
        fun fromMap(jobMap: Map<String, Any>) : Job {
            val tagMap = jobMap["tags"] as List<Map<String, String>>
            val tags = tagMap.map { Tag(id = null, name = it["name"].toString()) }.toMutableList()

            return Job(id = null,
                hourlyRate = (jobMap["hourlyRate"] as Double).toFloat(),
                description = jobMap["description"] as String,
                position = jobMap["position"] as String,
                tags = tags
            )
        }
    }
}

fun ResultRow.toJob() = Job(
    id = this[Jobs.id].value,
    position = this[Jobs.position],
    description = this[Jobs.description],
    hourlyRate = this[Jobs.hourlyRate],
    createdBy = this.toUser(),
    createdAt = this[Jobs.createdAt]
)



