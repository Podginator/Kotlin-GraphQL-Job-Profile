package org.skyworkz.profile.messages.processors.notifications

import org.skyworkz.profile.domain.model.JobUpdate
import org.skyworkz.profile.domain.model.User

interface Notifier {
    fun sendNotification(userNotifications : Map<User, List<JobUpdate>>)
}