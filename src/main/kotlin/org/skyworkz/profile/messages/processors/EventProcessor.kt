package org.skyworkz.profile.messages.processors

import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.messages.events.Event
import org.skyworkz.profile.messages.processors.notifications.EmailNotifier
import org.skyworkz.profile.messages.processors.notifications.WebAppNotifier

class EventProcessor(private val jobAppropriateProfileExtractor: JobAppropriateProfileExtractor,
                     private val emailNotifier: EmailNotifier,
                     private val webAppNotifier: WebAppNotifier) : IEventProcessor {

    override fun process(event: Event) {
        // First Check if we are a job based event.
        when (event) {
            is Event.JobCreated -> processJob(event.job)
        }
    }

    private fun processJob(job: Job) {
        val notifications = jobAppropriateProfileExtractor.extractAppropriateUsersFromJob(job)
        emailNotifier.sendNotification(notifications)
        webAppNotifier.sendNotification(notifications)
    }
}