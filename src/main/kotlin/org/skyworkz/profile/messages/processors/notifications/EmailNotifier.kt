package org.skyworkz.profile.messages.processors.notifications

import org.skyworkz.profile.domain.model.JobUpdate
import org.skyworkz.profile.domain.model.User
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*

class EmailNotifier(private val sesClient: SesClient) : Notifier {

    private val HOST_NAME = "http://localhost:4100"
    private val FROM = "tom@skyworkz.nl"

    fun getEmailTitle(jobUpdates: List<JobUpdate>) : String {
        val names = jobUpdates.map { it.profile.firstName + " " + it.profile.lastName }.joinToString(",")
        return "Workz: New Job Updates for $names"
    }

    fun getEmailContent(jobUpdates: List<JobUpdate>) : String {
        val jobAndUserPairs = jobUpdates
            .map { "<li><a href=\"${HOST_NAME}/${it.profile.email}\">${it.profile.firstName} ${it.profile.lastName}:</a> <a href=\"${HOST_NAME}/job/${it.job.id}\">${it.job.position}</a></li>" }
            .joinToString()
        return """
            <h1>The following users have jobs that may be appropriate for them:</h1>
            <ul>
                ${jobAndUserPairs}
            </ul>
        """.trimIndent()
    }

    // Create an email template and send to the subscribed users.
    override fun sendNotification(userNotifications: Map<User, List<JobUpdate>>) {
        println(userNotifications)
        val emailRequests = userNotifications.map { jobUpdate ->
            SendEmailRequest
                .builder()
                .destination(Destination.builder().toAddresses(jobUpdate.key.email).build())
                .message(
                    Message.builder()
                        .body(
                            Body.builder()
                                .html(
                                    Content.builder().charset("UTF-8").data(getEmailContent(jobUpdate.value)).build()
                                )
                                .build()
                        )
                        .subject(
                            Content.builder().charset("UTF-8").data(getEmailTitle(jobUpdate.value)).build()
                        )
                        .build()
                )
                .source(FROM)
                .build()
        }

        try {
            emailRequests.forEach { sesClient.sendEmail(it) }
        } catch (e : Exception) {
            println(e)
            throw e
        }
    }
}