package org.skyworkz.profile.web.controllers

import io.javalin.http.Context
import org.skyworkz.profile.domain.model.request.GraphQLRequest
import org.skyworkz.profile.domain.repository.INotificationRepository
import org.skyworkz.profile.messages.processors.notifications.NotificationInformation

class WebNotificationController(private val notificationRepository: INotificationRepository) {
    fun post (ctx: Context) {
        ctx.bodyAsClass(NotificationInformation::class.java).let {
            notificationRepository.upsert(it)
        }
    }
}