package org.skyworkz.profile.messages.events

import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import java.util.*

sealed class Event(val eventId: String = UUID.randomUUID().toString()) {
    class JobCreated(val job : Job) : Event()
    class UserCreated(val user: User) : Event()
}