package org.skyworkz.profile.domain.model

data class User(
    var id: Int? = 0,
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String? = "",
    var available: Boolean? = false,
    val minimumHourlyRate: Float? = 0.0f,
    val tags: MutableList<Tag> = mutableListOf(),
    val approved: Boolean = false
)