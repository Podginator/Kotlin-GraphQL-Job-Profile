package org.skyworkz.profile.messages.processors.notifications

import org.jetbrains.exposed.sql.ResultRow

data class NotificationInformation (
    val id: Int,
    val userAgent: String,
    val token: String
)
