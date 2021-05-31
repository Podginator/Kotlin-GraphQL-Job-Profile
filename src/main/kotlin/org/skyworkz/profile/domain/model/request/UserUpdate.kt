package org.skyworkz.profile.domain.model.request

import org.skyworkz.profile.domain.model.Tag

data class UserUpdate(
    var firstName: String?,
    var lastName: String?,
    var bio: String? = "",
    var available: Boolean?,
    val minimumHourlyRate: Float?,
    val tags: MutableList<Tag>?,
    val approved: Boolean?
) {
    companion object {
        fun fromMap(updateMap:  Map<String, Any?>) : UserUpdate {
            val tagMap = updateMap["tags"] as List<Map<String, String>>?
            val tags = tagMap?.map { Tag(id = null, name = it["name"].toString()) }?.toMutableList()

            return UserUpdate(
                firstName = updateMap["firstName"]?.toString(),
                lastName = updateMap["lastName"]?.toString(),
                bio = updateMap["bio"]?.toString(),
                available = updateMap["available"] as? Boolean,
                minimumHourlyRate = (updateMap["minimumHourlyRate"] as? Double)?.toFloat(),
                tags = tags,
                approved = updateMap["approved"] as? Boolean)
        }
    }
}