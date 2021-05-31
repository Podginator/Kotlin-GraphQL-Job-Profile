package org.skyworkz.profile.messages

import org.skyworkz.profile.messages.events.Event

interface IEventBroker {
    fun commit(event: Event)
}