package org.skyworkz.profile.messages.processors.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.skyworkz.profile.domain.model.JobUpdate
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.INotificationRepository

class WebAppNotifier(private val repository: INotificationRepository,
                     private val firebase: FirebaseMessaging
) : Notifier {


    private fun getContent(update: List<JobUpdate>) : String {
        val users = update.map { "${it.profile.firstName} ${it.profile.lastName}" }.joinToString(",")

        return "Jobs for $users"
    }

    private fun getFirebaseMessageFromUserNotification(jobUpdate : Map.Entry<User, List<JobUpdate>>) : List<Message> {
        val profile = jobUpdate.key
        val updates = jobUpdate.value

        return repository.getById(profile.id!!).mapNotNull {
            Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle("Workz Job Updates")
                        .setBody(getContent(updates))
                        .setImage("skyworkz.png")
                        .build()
                )
                .putData("link", "${System.getenv("HOST_URL")}/${profile.email}")
                .setToken(it.token)
                .build()
        }
    }

    override fun sendNotification(userNotifications: Map<User, List<JobUpdate>>) {
        val messages : List<Message> = userNotifications
            .mapNotNull { getFirebaseMessageFromUserNotification(it) }
            .flatten()

        if (messages.isNotEmpty()) {
            firebase.sendAll(messages)
        }
    }

}