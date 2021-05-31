package org.skyworkz.profile.domain.repository

import org.skyworkz.profile.messages.processors.notifications.NotificationInformation

interface INotificationRepository {
    fun upsert(notification: NotificationInformation) : NotificationInformation
    fun getById(id : Int) : List<NotificationInformation>
}